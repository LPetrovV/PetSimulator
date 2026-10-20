/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author AJ Franchuk
 */
//This class is the database component - every read from and write to the
//embedded Derby database happens here through JDBC, so nothing else in the
//game has to know any SQL. DatabaseManager hands out the connection.
//
//  READ  1  loadSave()          GAME_SAVE + INVENTORY   SELECT
//  READ  2  loadItems()         ITEM_CATALOG            SELECT
//  READ  3  readLog()           ACTIVITY_LOG            SELECT
//  WRITE 1  save()              GAME_SAVE + INVENTORY   DELETE + INSERT (one transaction)
//  WRITE 2  seedDefaultItems()  ITEM_CATALOG            INSERT (batch)
//  WRITE 3  appendLog()         ACTIVITY_LOG            INSERT
//  WRITE 4  deleteSave()        GAME_SAVE + INVENTORY   DELETE
//
//Nothing in here is allowed to crash the game: a database error is printed
//and the method returns a safe value instead of throwing.
//
//AI-assisted (Claude): this class used to read and write text files in a
//data folder. The JDBC version was written with AI help, keeping the same
//public methods so Game and GameController barely had to change.
public class SaveSystem {

    //There is only one save slot in this game, so it always has this id
    public static final int SAVE_SLOT = 1;

    private static final DateTimeFormatter STAMP
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DatabaseManager database;

    //The snapshot most recently written or read
    private SaveData saveData;

    //The real game database
    public SaveSystem() {
        this(new DatabaseManager());
    }

    //Lets the unit tests point the save system at a throwaway database
    public SaveSystem(DatabaseManager database) {
        this.database = database;
    }

    public SaveData getSaveData() {
        return saveData;
    }

    //True if there is a saved game worth offering to the player
    public boolean exists() {
        String sql = "SELECT COUNT(*) FROM GAME_SAVE WHERE SAVE_ID = ?";
        try (PreparedStatement query = database.getConnection().prepareStatement(sql)) {
            query.setInt(1, SAVE_SLOT);
            try (ResultSet result = query.executeQuery()) {
                return result.next() && result.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Could not check for a saved game: " + e.getMessage());
            return false;
        }
    }

    //WRITE 4 - removes the save, used when the pet has left and the game is over
    //The INVENTORY rows go with it because of ON DELETE CASCADE
    public boolean deleteSave() {
        String sql = "DELETE FROM GAME_SAVE WHERE SAVE_ID = ?";
        try (PreparedStatement delete = database.getConnection().prepareStatement(sql)) {
            delete.setInt(1, SAVE_SLOT);
            return delete.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Could not delete the saved game: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // The saved game
    // =====================================================================

    //WRITE 1 - writes the whole game into GAME_SAVE and INVENTORY
    //The old save and its items are removed and the new ones inserted inside
    //one transaction, so a crash half way through can never leave a save with
    //the wrong items - either everything is written or nothing is.
    public boolean save(SaveData data) {
        //Nothing to write - return before the old save is touched, otherwise the
        //DELETE below would be committed with no new save to replace it
        if (data == null) {
            return false;
        }
        this.saveData = data;
        Connection connection = null;

        try {
            connection = database.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM GAME_SAVE WHERE SAVE_ID = ?")) {
                delete.setInt(1, SAVE_SLOT);
                delete.executeUpdate();
            }

            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO GAME_SAVE (SAVE_ID, PLAYER_NAME, PLAYER_COINS, "
                    + "PET_TYPE, PET_NAME, PET_AGE, PET_ENERGY, PET_HUNGER, "
                    + "PET_HAPPINESS, TREE_APPLES, TREE_GROWTH, FIRST_PLAYED, "
                    + "SYSTEM_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                insert.setInt(1, SAVE_SLOT);
                insert.setString(2, fit(data.getPlayerName(), 100));
                insert.setInt(3, data.getPlayerCoins());
                insert.setString(4, fit(data.getPetType(), 30));
                insert.setString(5, fit(data.getPetName(), 100));
                insert.setInt(6, data.getPetAge());
                insert.setInt(7, data.getPetEnergy());
                insert.setInt(8, data.getPetHunger());
                insert.setInt(9, data.getPetHappiness());
                insert.setInt(10, data.getTreeApples());
                insert.setInt(11, data.getTreeGrowth());
                insert.setTimestamp(12, Timestamp.valueOf(data.getFirstPlayed()));
                insert.setTimestamp(13, Timestamp.valueOf(data.getSystemDate()));
                insert.executeUpdate();
            }

            //Each item in SaveData looks like "FoodItem:apple:2:10"
            try (PreparedStatement insertItem = connection.prepareStatement(
                    "INSERT INTO INVENTORY (SAVE_ID, ITEM_TYPE, ITEM_NAME, PRICE, BUFF) "
                    + "VALUES (?, ?, ?, ?, ?)")) {
                for (String record : data.getItems()) {
                    String[] parts = record.split(":");
                    if (parts.length < 4) {
                        continue; //damaged record, skip it
                    }
                    try {
                        insertItem.setInt(1, SAVE_SLOT);
                        insertItem.setString(2, fit(parts[0].trim(), 30));
                        insertItem.setString(3, fit(parts[1].trim(), 50));
                        insertItem.setInt(4, Integer.parseInt(parts[2].trim()));
                        insertItem.setInt(5, Integer.parseInt(parts[3].trim()));
                        insertItem.addBatch();
                    } catch (NumberFormatException e) {
                        //a bad number - skip this item rather than lose the save
                    }
                }
                insertItem.executeBatch();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Could not save the game: " + e.getMessage());
            rollback(connection);
            return false;
        } finally {
            restoreAutoCommit(connection);
        }
    }

    //READ 1 - reads the saved game and its items back out of the database
    public SaveData loadSave() {
        SaveData data = new SaveData();

        try {
            Connection connection = database.getConnection();

            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM GAME_SAVE WHERE SAVE_ID = ?")) {
                query.setInt(1, SAVE_SLOT);
                try (ResultSet row = query.executeQuery()) {
                    if (!row.next()) {
                        return null; //nothing saved yet
                    }
                    data.setPlayerName(row.getString("PLAYER_NAME"));
                    data.setPlayerCoins(row.getInt("PLAYER_COINS"));
                    data.setPetType(row.getString("PET_TYPE"));
                    data.setPetName(row.getString("PET_NAME"));
                    data.setPetAge(row.getInt("PET_AGE"));
                    data.setPetEnergy(row.getInt("PET_ENERGY"));
                    data.setPetHunger(row.getInt("PET_HUNGER"));
                    data.setPetHappiness(row.getInt("PET_HAPPINESS"));
                    data.setTreeApples(row.getInt("TREE_APPLES"));
                    data.setTreeGrowth(row.getInt("TREE_GROWTH"));
                    data.setFirstPlayed(row.getTimestamp("FIRST_PLAYED").toLocalDateTime());
                    data.setSystemDate(row.getTimestamp("SYSTEM_DATE").toLocalDateTime());
                }
            }

            //ORDER BY keeps the items in the order the player had them
            try (PreparedStatement query = connection.prepareStatement(
                    "SELECT ITEM_TYPE, ITEM_NAME, PRICE, BUFF FROM INVENTORY "
                    + "WHERE SAVE_ID = ? ORDER BY ITEM_ID")) {
                query.setInt(1, SAVE_SLOT);
                try (ResultSet row = query.executeQuery()) {
                    while (row.next()) {
                        data.getItems().add(row.getString("ITEM_TYPE") + ":"
                                + row.getString("ITEM_NAME") + ":"
                                + row.getInt("PRICE") + ":" + row.getInt("BUFF"));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not load the saved game: " + e.getMessage());
            return null;
        }

        this.saveData = data;
        return data;
    }

    //Rebuilds the saved items into real objects
    //Each record looks like "FoodItem:apple:2:10"
    public ArrayList<Item> rebuildItems(SaveData data) {
        ArrayList<Item> rebuilt = new ArrayList<>();

        for (String record : data.getItems()) {
            String[] parts = record.split(":");
            if (parts.length < 4) {
                continue; //damaged record, skip it
            }
            try {
                String type = parts[0].trim();
                String name = parts[1].trim();
                int price = Integer.parseInt(parts[2].trim());
                int buff = Integer.parseInt(parts[3].trim());

                if (type.equals("ToyItem")) {
                    rebuilt.add(new ToyItem(name, price, buff));
                } else {
                    rebuilt.add(new FoodItem(name, price, buff));
                }
            } catch (NumberFormatException e) {
                //a bad number - skip this item rather than crash
            }
        }
        return rebuilt;
    }

    // =====================================================================
    // The item catalog
    // =====================================================================

    //READ 2 - loads the starting items from ITEM_CATALOG, so the items in the
    //game can be changed in the database without touching any code
    public ArrayList<Item> loadItems() {
        ArrayList<Item> loaded = new ArrayList<>();

        try {
            if (countRows("ITEM_CATALOG") == 0) {
                seedDefaultItems();
            }

            try (Statement statement = database.getConnection().createStatement();
                    ResultSet row = statement.executeQuery(
                            "SELECT ITEM_TYPE, ITEM_NAME, PRICE, BUFF, QUANTITY "
                            + "FROM ITEM_CATALOG ORDER BY ITEM_TYPE, ITEM_NAME")) {
                while (row.next()) {
                    String type = row.getString("ITEM_TYPE");
                    String name = row.getString("ITEM_NAME");
                    int price = row.getInt("PRICE");
                    int buff = row.getInt("BUFF");
                    int howMany = row.getInt("QUANTITY");

                    for (int i = 0; i < howMany; i++) {
                        if (type.equalsIgnoreCase("toy")) {
                            loaded.add(new ToyItem(name, price, buff));
                        } else {
                            loaded.add(new FoodItem(name, price, buff));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not load the item catalog, starting with an empty bag: "
                    + e.getMessage());
        }
        return loaded;
    }

    //WRITE 2 - fills ITEM_CATALOG the first time the game runs
    //Food is mostly grown on the apple tree, so the player only starts with a
    //little. Toys are not used up, so a few last the whole game.
    public void seedDefaultItems() {
        Object[][] defaults = {
            {"food", "apple", 2, 10, 2},
            {"toy", "ball", 5, 20, 1},
            {"toy", "mouse", 4, 15, 1},
            {"toy", "bell", 7, 25, 1}
        };

        String sql = "INSERT INTO ITEM_CATALOG (ITEM_TYPE, ITEM_NAME, PRICE, BUFF, QUANTITY) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insert = database.getConnection().prepareStatement(sql)) {
            for (Object[] item : defaults) {
                insert.setString(1, (String) item[0]);
                insert.setString(2, (String) item[1]);
                insert.setInt(3, (Integer) item[2]);
                insert.setInt(4, (Integer) item[3]);
                insert.setInt(5, (Integer) item[4]);
                insert.addBatch();
            }
            insert.executeBatch();
        } catch (SQLException e) {
            System.out.println("Could not create the item catalog: " + e.getMessage());
        }
    }

    // =====================================================================
    // The activity log
    // =====================================================================

    //WRITE 3 - adds one timestamped row to the log
    public void appendLog(String message) {
        String sql = "INSERT INTO ACTIVITY_LOG (LOGGED_AT, MESSAGE) VALUES (?, ?)";
        try (PreparedStatement insert = database.getConnection().prepareStatement(sql)) {
            insert.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            insert.setString(2, fit(message, 500));
            insert.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not write to the log: " + e.getMessage());
        }
    }

    //READ 3 - reads the most recent rows back out of the log, oldest first,
    //formatted the same way the old log.txt lines were
    public ArrayList<String> readLog(int howMany) {
        ArrayList<String> recent = new ArrayList<>();

        //AI-assisted (Claude): FETCH FIRST n ROWS ONLY is the Derby version of
        //LIMIT. Sorting newest first and then reversing in Java gives the last
        //n rows in the right order without reading the whole table.
        String sql = "SELECT LOGGED_AT, MESSAGE FROM ACTIVITY_LOG "
                + "ORDER BY LOG_ID DESC FETCH FIRST ? ROWS ONLY";
        try (PreparedStatement query = database.getConnection().prepareStatement(sql)) {
            query.setInt(1, Math.max(howMany, 1));
            try (ResultSet row = query.executeQuery()) {
                while (row.next()) {
                    recent.add(row.getTimestamp("LOGGED_AT").toLocalDateTime().format(STAMP)
                            + " | " + row.getString("MESSAGE"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not read the log: " + e.getMessage());
        }

        Collections.reverse(recent);
        return recent;
    }

    // =====================================================================
    // Small helpers
    // =====================================================================

    private int countRows(String table) throws SQLException {
        try (Statement statement = database.getConnection().createStatement();
                ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return result.next() ? result.getInt(1) : 0;
        }
    }

    //Cuts text down to the column size so a very long name cannot fail a save
    private String fit(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max);
    }

    private void rollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Could not undo the failed save: " + e.getMessage());
        }
    }

    private void restoreAutoCommit(Connection connection) {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            //the connection is already broken, nothing more to do
        }
    }
}
