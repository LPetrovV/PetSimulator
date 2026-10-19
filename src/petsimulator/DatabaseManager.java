/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author AJ Franchuk
 */
//This class owns the connection to the embedded Apache Derby database. It is
//the only class that knows the JDBC URL, user name and password, and it makes
//sure every table exists before SaveSystem tries to use it.
//
//The database lives in the "database" folder inside the project, so it can be
//zipped up and handed in with the rest of the code.
//
//  GAME_SAVE     one row per save slot - the player, the pet and the tree
//  INVENTORY     one row per item the player owns, linked to GAME_SAVE
//  ITEM_CATALOG  the starting items a new game is given
//  ACTIVITY_LOG  a timestamped history of new games, saves and loads
//
//AI-assisted (Claude): the table layout and the "check DatabaseMetaData before
//CREATE TABLE" approach were suggested by AI. The item list was moved out of
//the save into its own INVENTORY table instead of one long text column like
//the old save file used.
public class DatabaseManager {

    public static final String DB_FOLDER = "database";
    public static final String DB_NAME = "PetSimulatorDB";

    //Embedded Derby does not check passwords unless authentication is turned
    //on, but the user name still decides the schema the tables are made in
    public static final String USER = "petsim";
    public static final String PASSWORD = "petsim";

    private final String dbPath;
    private Connection connection;

    //Only one shutdown hook is ever needed, however many managers are made
    private static boolean shutdownHookAdded = false;

    //The real game database: database/PetSimulatorDB
    public DatabaseManager() {
        this(DB_FOLDER + File.separator + DB_NAME);
    }

    //Any other location, used by the unit tests so they never touch real saves
    public DatabaseManager(String dbPath) {
        this.dbPath = dbPath;

        //Derby writes a derby.log next to wherever the program was started.
        //Keep it in the database folder instead so the project stays tidy.
        File parent = new File(dbPath).getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (System.getProperty("derby.stream.error.file") == null && parent != null) {
            System.setProperty("derby.stream.error.file",
                    new File(parent, "derby.log").getPath());
        }
        addShutdownHook();
    }

    public String getUrl() {
        return "jdbc:derby:" + dbPath + ";create=true";
    }

    //Opens the connection the first time it is needed and reuses it after that
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(getUrl(), USER, PASSWORD);
            createTables();
        }
        return connection;
    }

    //Creates any table that is missing - the first run builds the whole
    //database, every run after that finds the tables already there
    private void createTables() throws SQLException {
        createTableIfMissing("GAME_SAVE",
                "CREATE TABLE GAME_SAVE ("
                + "SAVE_ID INT PRIMARY KEY, "
                + "PLAYER_NAME VARCHAR(100) NOT NULL, "
                + "PLAYER_COINS INT NOT NULL, "
                + "PET_TYPE VARCHAR(30) NOT NULL, "
                + "PET_NAME VARCHAR(100) NOT NULL, "
                + "PET_AGE INT NOT NULL, "
                + "PET_ENERGY INT NOT NULL, "
                + "PET_HUNGER INT NOT NULL, "
                + "PET_HAPPINESS INT NOT NULL, "
                + "TREE_APPLES INT NOT NULL, "
                + "TREE_GROWTH INT NOT NULL, "
                + "FIRST_PLAYED TIMESTAMP NOT NULL, "
                + "SYSTEM_DATE TIMESTAMP NOT NULL)");

        //ON DELETE CASCADE means deleting a save also removes its items
        createTableIfMissing("INVENTORY",
                "CREATE TABLE INVENTORY ("
                + "ITEM_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                + "SAVE_ID INT NOT NULL REFERENCES GAME_SAVE(SAVE_ID) ON DELETE CASCADE, "
                + "ITEM_TYPE VARCHAR(30) NOT NULL, "
                + "ITEM_NAME VARCHAR(50) NOT NULL, "
                + "PRICE INT NOT NULL, "
                + "BUFF INT NOT NULL)");

        createTableIfMissing("ITEM_CATALOG",
                "CREATE TABLE ITEM_CATALOG ("
                + "ITEM_TYPE VARCHAR(10) NOT NULL, "
                + "ITEM_NAME VARCHAR(50) NOT NULL, "
                + "PRICE INT NOT NULL, "
                + "BUFF INT NOT NULL, "
                + "QUANTITY INT NOT NULL, "
                + "PRIMARY KEY (ITEM_TYPE, ITEM_NAME))");

        createTableIfMissing("ACTIVITY_LOG",
                "CREATE TABLE ACTIVITY_LOG ("
                + "LOG_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                + "LOGGED_AT TIMESTAMP NOT NULL, "
                + "MESSAGE VARCHAR(500) NOT NULL)");
    }

    private void createTableIfMissing(String table, String sql) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        //Derby stores unquoted names in upper case, in the schema named after the user
        try (ResultSet tables = meta.getTables(null, USER.toUpperCase(), table, null)) {
            if (tables.next()) {
                return; //already there
            }
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    //Closes this connection, the database itself stays booted
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Could not close the database: " + e.getMessage());
        }
    }

    //Shuts the whole Derby engine down cleanly so nothing is half written.
    //Derby reports a successful shutdown by throwing an SQLException with the
    //state XJ015, so that one is expected and ignored.
    public static void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (!"XJ015".equals(e.getSQLState())) {
                System.out.println("Database did not shut down cleanly: " + e.getMessage());
            }
        }
    }

    //AI-assisted (Claude): the window closes with EXIT_ON_CLOSE, so there is no
    //single place in the GUI code to shut the database down. A JVM shutdown
    //hook runs on every exit, including the X button.
    private static synchronized void addShutdownHook() {
        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::shutdown));
            shutdownHookAdded = true;
        }
    }
}
