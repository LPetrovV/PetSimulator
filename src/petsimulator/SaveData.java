/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author AJ Franchuk
 */
//Holds a flat snapshot of the game so SaveSystem has something simple to write
//out. Player and Pet stay clean - they never need to know about files.
public class SaveData {

    private String playerName;
    private int playerCoins;

    private String petType;
    private String petName;
    private int petAge;
    private int petEnergy;
    private int petHunger;
    private int petHappiness;

    //The apple tree, so fruit is not lost or duplicated between sessions
    private int treeApples;
    private int treeGrowth;

    //The real time the pet was adopted - the pet's age is measured from here
    private LocalDateTime firstPlayed;
    //The real system time when the save was written. On the next load this is
    //what TimeSystem compares against to work out how long the player was away.
    private LocalDateTime systemDate;

    //Each entry is one item, stored as "ClassName:name:price:buff"
    private ArrayList<String> items = new ArrayList<>();

    //Builds a snapshot from the live game, ready to be saved
    public SaveData(Player player, Pet pet, TimeSystem time) {
        this.playerName = player.getName();
        this.playerCoins = player.getCoins();

        this.petType = pet.getClass().getSimpleName();
        this.petName = pet.getName();
        this.petAge = pet.getAge();
        this.petEnergy = pet.getNeeds().getEnergy();
        this.petHunger = pet.getNeeds().getHunger();
        this.petHappiness = pet.getNeeds().getHappiness();

        this.firstPlayed = time.getFirstPlayed();

        this.treeApples = player.getGarden().getApples();
        this.treeGrowth = player.getGarden().getGrowth();

        this.systemDate = LocalDateTime.now();

        for (Item item : player.getInventory().getItems()) {
            this.items.add(item.getClass().getSimpleName() + ":"
                    + item.getName() + ":" + item.getPrice() + ":" + item.getBuff());
        }
    }

    //Empty snapshot, filled in by SaveSystem while it reads the file
    public SaveData() {
        this.playerName = "Player";
        this.playerCoins = 0;
        this.petType = "Pet";
        this.petName = "Pet";
        this.petAge = 0;
        this.petEnergy = 100;
        this.petHunger = 100;
        this.petHappiness = 100;
        this.firstPlayed = LocalDateTime.now();
        this.treeApples = 2;
        this.treeGrowth = 0;
        this.systemDate = LocalDateTime.now();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerCoins() {
        return playerCoins;
    }

    public void setPlayerCoins(int playerCoins) {
        this.playerCoins = playerCoins;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public int getPetAge() {
        return petAge;
    }

    public void setPetAge(int petAge) {
        this.petAge = petAge;
    }

    public int getPetEnergy() {
        return petEnergy;
    }

    public void setPetEnergy(int petEnergy) {
        this.petEnergy = petEnergy;
    }

    public int getPetHunger() {
        return petHunger;
    }

    public void setPetHunger(int petHunger) {
        this.petHunger = petHunger;
    }

    public int getPetHappiness() {
        return petHappiness;
    }

    public void setPetHappiness(int petHappiness) {
        this.petHappiness = petHappiness;
    }

    public LocalDateTime getFirstPlayed() {
        return firstPlayed;
    }

    public void setFirstPlayed(LocalDateTime firstPlayed) {
        this.firstPlayed = firstPlayed;
    }

    public int getTreeApples() {
        return treeApples;
    }

    public void setTreeApples(int treeApples) {
        this.treeApples = treeApples;
    }

    public int getTreeGrowth() {
        return treeGrowth;
    }

    public void setTreeGrowth(int treeGrowth) {
        this.treeGrowth = treeGrowth;
    }

    public LocalDateTime getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(LocalDateTime systemDate) {
        this.systemDate = systemDate;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    //Turns the item list into one line for the save file
    public String getItemsAsLine() {
        return String.join(";", items);
    }

    //Reads that line back into the item list
    public void setItemsFromLine(String line) {
        items.clear();
        if (line == null || line.isBlank()) {
            return;
        }
        for (String record : line.split(";")) {
            if (!record.isBlank()) {
                items.add(record.trim());
            }
        }
    }
}
