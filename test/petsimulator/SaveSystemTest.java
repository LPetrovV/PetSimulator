/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package petsimulator;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author AJ Franchuk
 */
//Tests the JDBC save system against its own throwaway Derby database in
//test-database/, so running the tests never touches a real saved game.
//
//AI-assisted (Claude): the test cases were drafted with AI and checked by
//hand against what Game.java actually calls.
public class SaveSystemTest {

    private static DatabaseManager database;
    private static SaveSystem saveSystem;

    @BeforeClass
    public static void openDatabase() {
        database = new DatabaseManager("test-database" + File.separator + "PetSimulatorTestDB");
        saveSystem = new SaveSystem(database);
    }

    @AfterClass
    public static void closeDatabase() {
        database.close();
    }

    //Every test starts with empty tables
    @Before
    public void clearTables() throws SQLException {
        Connection connection = database.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM INVENTORY");
            statement.executeUpdate("DELETE FROM GAME_SAVE");
            statement.executeUpdate("DELETE FROM ITEM_CATALOG");
            statement.executeUpdate("DELETE FROM ACTIVITY_LOG");
        }
    }

    private SaveData sampleSave() {
        SaveData data = new SaveData();
        data.setPlayerName("Lisa");
        data.setPlayerCoins(42);
        data.setPetType("Cat");
        data.setPetName("Whiskers");
        data.setPetAge(3);
        data.setPetEnergy(80);
        data.setPetHunger(55);
        data.setPetHappiness(70);
        data.setTreeApples(4);
        data.setTreeGrowth(2);
        data.setFirstPlayed(LocalDateTime.of(2026, 10, 1, 9, 30, 0));
        data.setSystemDate(LocalDateTime.of(2026, 10, 4, 18, 15, 0));
        data.getItems().add("FoodItem:apple:2:10");
        data.getItems().add("ToyItem:ball:5:20");
        return data;
    }

    @Test
    public void testExists_NoSaveAtFirst() {
        assertFalse(saveSystem.exists());
        assertNull(saveSystem.loadSave());
    }

    @Test
    public void testSaveAndLoad_AllFieldsMatch() {
        assertTrue(saveSystem.save(sampleSave()));
        assertTrue(saveSystem.exists());

        SaveData loaded = saveSystem.loadSave();
        assertNotNull(loaded);
        assertEquals("Lisa", loaded.getPlayerName());
        assertEquals(42, loaded.getPlayerCoins());
        assertEquals("Cat", loaded.getPetType());
        assertEquals("Whiskers", loaded.getPetName());
        assertEquals(3, loaded.getPetAge());
        assertEquals(80, loaded.getPetEnergy());
        assertEquals(55, loaded.getPetHunger());
        assertEquals(70, loaded.getPetHappiness());
        assertEquals(4, loaded.getTreeApples());
        assertEquals(2, loaded.getTreeGrowth());
        assertEquals(LocalDateTime.of(2026, 10, 1, 9, 30, 0), loaded.getFirstPlayed());
        assertEquals(LocalDateTime.of(2026, 10, 4, 18, 15, 0), loaded.getSystemDate());
        assertEquals(2, loaded.getItems().size());
        assertEquals("FoodItem:apple:2:10", loaded.getItems().get(0));
        assertEquals("ToyItem:ball:5:20", loaded.getItems().get(1));
    }

    @Test
    public void testSave_ReplacesOldSave() {
        saveSystem.save(sampleSave());

        SaveData second = sampleSave();
        second.setPlayerCoins(7);
        second.getItems().clear();
        second.getItems().add("ToyItem:bell:7:25");
        saveSystem.save(second);

        SaveData loaded = saveSystem.loadSave();
        assertEquals(7, loaded.getPlayerCoins());
        assertEquals(1, loaded.getItems().size());
        assertEquals("ToyItem:bell:7:25", loaded.getItems().get(0));
    }

    @Test
    public void testRebuildItems_RightClass() {
        saveSystem.save(sampleSave());
        ArrayList<Item> items = saveSystem.rebuildItems(saveSystem.loadSave());
        assertEquals(2, items.size());
        assertTrue(items.get(0) instanceof FoodItem);
        assertTrue(items.get(1) instanceof ToyItem);
        assertEquals("ball", items.get(1).getName());
    }

    @Test
    public void testDeleteSave_RemovesSaveAndItems() throws SQLException {
        saveSystem.save(sampleSave());
        assertTrue(saveSystem.deleteSave());
        assertFalse(saveSystem.exists());

        try (Statement statement = database.getConnection().createStatement();
                java.sql.ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM INVENTORY")) {
            result.next();
            assertEquals(0, result.getInt(1)); //ON DELETE CASCADE removed them
        }
    }

    @Test
    public void testLoadItems_DefaultsAddedOnce() {
        ArrayList<Item> items = saveSystem.loadItems();
        //2 apples + ball + mouse + bell
        assertEquals(5, items.size());

        //a second load must not add the defaults twice
        assertEquals(5, saveSystem.loadItems().size());
    }

    @Test
    public void testReadLog_NewestInOrder() {
        saveSystem.appendLog("first");
        saveSystem.appendLog("second");
        saveSystem.appendLog("third");

        ArrayList<String> lastTwo = saveSystem.readLog(2);
        assertEquals(2, lastTwo.size());
        assertTrue(lastTwo.get(0).endsWith("| second"));
        assertTrue(lastTwo.get(1).endsWith("| third"));
    }

    @Test
    public void testSave_VeryLongName() {
        SaveData data = sampleSave();
        data.setPlayerName("x".repeat(300));
        assertTrue(saveSystem.save(data));
        assertEquals(100, saveSystem.loadSave().getPlayerName().length());
    }

    @Test
    public void testDeleteSave_NothingToDelete() {
        assertFalse(saveSystem.deleteSave());
    }

    @Test
    public void testSave_NoItems() {
        SaveData data = sampleSave();
        data.getItems().clear();
        assertTrue(saveSystem.save(data));
        assertEquals(0, saveSystem.loadSave().getItems().size());
    }

    @Test
    public void testSave_DamagedItemIsSkipped() {
        SaveData data = sampleSave();
        data.getItems().add("ToyItem:broken");
        data.getItems().add("FoodItem:apple:two:10");
        saveSystem.save(data);
        assertEquals(2, saveSystem.loadSave().getItems().size());
    }

    @Test
    public void testRebuildItems_DamagedRecordIsSkipped() {
        SaveData data = new SaveData();
        data.getItems().add("FoodItem:apple:2:10");
        data.getItems().add("not an item");
        assertEquals(1, saveSystem.rebuildItems(data).size());
    }

    @Test
    public void testSaveAndLoad_RebuildsPetAndPlayer() {
        saveSystem.save(sampleSave());
        SaveData loaded = saveSystem.loadSave();
        PetNeeds needs = new PetNeeds(loaded.getPetEnergy(), loaded.getPetHunger(),
                loaded.getPetHappiness());
        Pet pet = new Cat(loaded.getPetName(), loaded.getPetAge(), needs);
        Player player = new Player(loaded.getPlayerName(), loaded.getPlayerCoins(),
                new AppleTree(loaded.getTreeApples(), loaded.getTreeGrowth()));
        assertEquals("Whiskers", pet.getName());
        assertEquals(55, pet.getNeeds().getHunger());
        assertEquals(42, player.getCoins());
        assertEquals(4, player.getGarden().getApples());
    }

    @Test
    public void testReadLog_EmptyLog() {
        assertEquals(0, saveSystem.readLog(5).size());
    }

    @Test(expected = NullPointerException.class)
    public void testRebuildItems_DataIsNull() {
        saveSystem.rebuildItems(null);
    }

    @Test
    public void testSave_DataIsNullKeepsOldSave() {
        saveSystem.save(sampleSave());
        assertFalse(saveSystem.save(null));
        assertEquals("Whiskers", saveSystem.loadSave().getPetName());
    }
}
