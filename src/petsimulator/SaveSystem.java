/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 *
 * @author AJ Franchuk
 */
//This class is the File I/O component - every read from and write to disk in
//the game happens here, so nothing else has to deal with files.
//
//  READ  1  loadSave()          data/savegame.txt   BufferedReader
//  READ  2  loadItems()         data/items.txt      Scanner
//  READ  3  readLog()           data/log.txt        BufferedReader
//  WRITE 1  saveToFile()        data/savegame.txt   PrintWriter
//  WRITE 2  writeDefaultItems() data/items.txt      PrintWriter
//  WRITE 3  appendLog()         data/log.txt        PrintWriter (append mode)
//
//Nothing in here is allowed to crash the game: a missing file is created with
//defaults, and a damaged line is skipped instead of throwing.
public class SaveSystem {

    public static final String FOLDER = "data";
    public static final String SAVE_FILE = FOLDER + File.separator + "savegame.txt";
    public static final String ITEMS_FILE = FOLDER + File.separator + "items.txt";
    public static final String LOG_FILE = FOLDER + File.separator + "log.txt";

    private static final DateTimeFormatter STAMP
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //The snapshot most recently written or read
    private SaveData saveData;

    public SaveData getSaveData() {
        return saveData;
    }

    //Makes sure the data folder exists before anything writes into it
    private void makeFolder() {
        File folder = new File(FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    //True if there is a save file worth offering to the player
    public boolean exists() {
        File file = new File(SAVE_FILE);
        return file.exists() && file.length() > 0;
    }

    //Removes the save, used when the pet has left and the game is over
    public boolean deleteSave() {
        File file = new File(SAVE_FILE);
        return file.exists() && file.delete();
    }

    // =====================================================================
    // The save file
    // =====================================================================

    //WRITE 1 - writes the whole game out as readable key=value text
    public boolean saveToFile(SaveData data) {
        makeFolder();
        this.saveData = data;

        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println("#Pet Simulator save file");
            writer.println("#firstPlayed is when the pet was adopted, systemDate is when");
            writer.println("#this file was written. Both are real world times.");
            writer.println("firstPlayed=" + data.getFirstPlayed().format(STAMP));
            writer.println("systemDate=" + data.getSystemDate().format(STAMP));
            writer.println();
            writer.println("playerName=" + data.getPlayerName());
            writer.println("playerCoins=" + data.getPlayerCoins());
            writer.println();
            writer.println("petType=" + data.getPetType());
            writer.println("petName=" + data.getPetName());
            writer.println("petAge=" + data.getPetAge());
            writer.println("petEnergy=" + data.getPetEnergy());
            writer.println("petHunger=" + data.getPetHunger());
            writer.println("petHappiness=" + data.getPetHappiness());
            writer.println();
            writer.println("treeApples=" + data.getTreeApples());
            writer.println("treeGrowth=" + data.getTreeGrowth());
            writer.println();
            writer.println("items=" + data.getItemsAsLine());
            return true;

        } catch (IOException e) {
            System.out.println("Could not save the game: " + e.getMessage());
            return false;
        }
    }

    //READ 1 - reads the save file back in
    //The lines go into a LinkedHashMap first, so the order of the file doesn't
    //matter and a missing line just falls back to a default
    public SaveData loadSave() {
        if (!exists()) {
            return null;
        }

        LinkedHashMap<String, String> values = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                int equals = trimmed.indexOf('=');
                //skip blank lines, comments and anything without a key
                if (!trimmed.isBlank() && !trimmed.startsWith("#") && equals > 0) {
                    values.put(trimmed.substring(0, equals).trim(),
                            trimmed.substring(equals + 1).trim());
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Could not read the save file: " + e.getMessage());
            return null;
        }

        if (values.isEmpty()) {
            System.out.println("The save file is empty or damaged.");
            return null;
        }

        SaveData data = new SaveData();
        data.setPlayerName(readText(values, "playerName", "Player"));
        data.setPlayerCoins(readNumber(values, "playerCoins", 0));
        data.setPetType(readText(values, "petType", "Pet"));
        data.setPetName(readText(values, "petName", "Pet"));
        data.setPetAge(readNumber(values, "petAge", 0));
        data.setPetEnergy(readNumber(values, "petEnergy", 100));
        data.setPetHunger(readNumber(values, "petHunger", 100));
        data.setPetHappiness(readNumber(values, "petHappiness", 100));
        data.setTreeApples(readNumber(values, "treeApples", 2));
        data.setTreeGrowth(readNumber(values, "treeGrowth", 0));
        data.setFirstPlayed(readDate(values, "firstPlayed"));
        data.setSystemDate(readDate(values, "systemDate"));
        data.setItemsFromLine(readText(values, "items", ""));

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
                //a hand-edited number - skip this item rather than crash
            }
        }
        return rebuilt;
    }

    // =====================================================================
    // The item list
    // =====================================================================

    //READ 2 - loads the starting items from a text file, so the items in the
    //game can be changed without touching any code
    //Format: type,name,price,buff,howMany
    public ArrayList<Item> loadItems() {
        if (!new File(ITEMS_FILE).exists()) {
            writeDefaultItems();
        }

        ArrayList<Item> loaded = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(ITEMS_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 5) {
                    continue; //not enough columns, skip the line
                }

                try {
                    String type = parts[0].trim();
                    String name = parts[1].trim();
                    int price = Integer.parseInt(parts[2].trim());
                    int buff = Integer.parseInt(parts[3].trim());
                    int howMany = Integer.parseInt(parts[4].trim());

                    for (int i = 0; i < howMany; i++) {
                        if (type.equalsIgnoreCase("toy")) {
                            loaded.add(new ToyItem(name, price, buff));
                        } else {
                            loaded.add(new FoodItem(name, price, buff));
                        }
                    }
                } catch (NumberFormatException e) {
                    //damaged line in the file, skip it
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No item file found, starting with an empty bag.");
        }
        return loaded;
    }

    //WRITE 2 - creates the item file the first time the game runs
    public void writeDefaultItems() {
        makeFolder();
        try (PrintWriter writer = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            writer.println("#Pet Simulator starting items");
            writer.println("#Format: type,name,price,buff,howMany");
            writer.println("#Food is mostly grown on the apple tree, so the");
            writer.println("#player only starts with a little");
            writer.println("food,apple,2,10,2");
    
            writer.println("#Toys are not used up, so a few last the whole game");
            writer.println("toy,ball,5,20,1");
            writer.println("toy,mouse,4,15,1");
            writer.println("toy,bell,7,25,1");
        } catch (IOException e) {
            System.out.println("Could not create the item file: " + e.getMessage());
        }
    }

    // =====================================================================
    // The activity log
    // =====================================================================

    //WRITE 3 - adds one timestamped line to the log
    //FileWriter is opened in append mode so old entries are kept
    public void appendLog(String message) {
        makeFolder();
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(LocalDateTime.now().format(STAMP) + " | " + message);
        } catch (IOException e) {
            System.out.println("Could not write to the log: " + e.getMessage());
        }
    }

    //READ 3 - reads the most recent lines back out of the log
    public ArrayList<String> readLog(int howMany) {
        ArrayList<String> all = new ArrayList<>();

        if (!new File(LOG_FILE).exists()) {
            return all;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line = reader.readLine();
            while (line != null) {
                if (!line.isBlank()) {
                    all.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Could not read the log: " + e.getMessage());
        }

        if (all.size() <= howMany) {
            return all;
        }
        return new ArrayList<>(all.subList(all.size() - howMany, all.size()));
    }

    // =====================================================================
    // Small helpers for reading values safely
    // =====================================================================

    private String readText(LinkedHashMap<String, String> values, String key, String fallback) {
        String value = values.get(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private int readNumber(LinkedHashMap<String, String> values, String key, int fallback) {
        try {
            return Integer.parseInt(readText(values, key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback; //the line was damaged, use the default
        }
    }

    private LocalDateTime readDate(LinkedHashMap<String, String> values, String key) {
        try {
            return LocalDateTime.parse(readText(values, key,
                    LocalDateTime.now().format(STAMP)), STAMP);
        } catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }
}
