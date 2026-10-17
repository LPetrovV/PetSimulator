/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov, modifications by AJ
 */
public class Game {

    private Player player;
    private Pet pet;
    SaveSystem gameSave = new SaveSystem();
    TimeSystem timeSystem = new TimeSystem();

    private String updateMessage = "";

    public void update() {
        //If the pet is gone there is nothing left to do
        //Bring the game up to date with the real world clock first, so the
        //needs on screen are always what they should be right now
        String news = timeSystem.update(pet, player.getGarden());
        if (!news.isEmpty()) {
            updateMessage += ("\n" + news);
        }

        //If the pet is gone there is nothing left to do
        if (!pet.isAlive()) {
            updateMessage += ("\n" + pet.getName() + " is no longer with you.\n");
            updateMessage += ("You kept them going for " + pet.getAge() + " day(s).");
            throw new QuitException();
        }

        //Warn the player while there is still time to do something about it
        if (pet.getNeeds().anyLow()) {
            updateMessage += ("WARNING: " + pet.getName() + " is "
                    + pet.getNeeds().worstNeed() + "! Look after them soon.");
        }
    }

    public void saveGame() {
        //save player and pet stats
        //SaveData also stores the system time, so the next load can work out
        //how long the player has been away
        SaveData data = new SaveData(player, pet, timeSystem);

        if (gameSave.saveToFile(data)) {
            //System.out.println("Game saved to " + SaveSystem.SAVE_FILE);
            gameSave.appendLog("SAVE | " + player.getName() + " saved "
                    + pet.getName() + " (age " + pet.getAge() + ")");
        }
    }

    public boolean loadGame() {
        //load from existing save file
        SaveData data = gameSave.loadSave();

        //if save file doesn't exist or can't be read, the caller starts a new game
        if (data == null) {
            return false;
        }

        //Rebuild the player, their garden and their inventory
        AppleTree garden = new AppleTree(data.getTreeApples(), data.getTreeGrowth());
        this.player = new Player(data.getPlayerName(), data.getPlayerCoins(), garden);
        for (Item item : gameSave.rebuildItems(data)) {
            player.getInventory().addItem(item);
        }

        //Rebuild the pet with the needs it had when the game was saved
        PetNeeds needs = new PetNeeds(data.getPetEnergy(), data.getPetHunger(),
                data.getPetHappiness());
        this.pet = createPet(data.getPetType(), data.getPetName(), data.getPetAge(), needs);

        //Restart the clock from the two real times in the save file: when the
        //pet was adopted, and when the game was last up to date
        this.timeSystem = new TimeSystem(data.getFirstPlayed(), data.getSystemDate());

        updateMessage += ("\nWelcome back, " + player.getName() + "!");

        //Apply all the real time that passed while the game was closed
        String news = timeSystem.update(pet, player.getGarden());
        if (!news.isEmpty()) {
            updateMessage += (news);
        }

        gameSave.appendLog("LOAD | " + player.getName() + " loaded "
                + pet.getName() + " (" + pet.getAge() + " day(s) old)");
        return true;
    }

    //Builds the right kind of pet from the type name stored in the save file
    //Adding a new species later means adding one line here
    private Pet createPet(String type, String name, int age, PetNeeds needs) {
        if (type.equalsIgnoreCase("Cat")) {
            return new Cat(name, age, needs);
        }
        return new Pet(name, age, needs);
    }

    public void quit() {

        if (pet != null && pet.isAlive()) {
            saveGame();
        } else if (pet != null) {
            //the pet has left, so there is nothing worth coming back to
            gameSave.deleteSave();
            gameSave.appendLog("LOST | " + pet.getName() + " left after "
                    + pet.getAge() + " day(s)");
        }
    }

    public void newGame(String petName, String playerName) {
        //Create new pet
        //change this to suit specific pet options
        this.pet = new Pet(petName);
        this.player = new Player(playerName);

        //Starting items are read from data/items.txt instead of being hard coded
        for (Item item : gameSave.loadItems()) {
            player.getInventory().addItem(item);
        }

        //A new pet starts the clock from right now
        this.timeSystem = new TimeSystem();

        updateMessage += ("\nThis game runs on real world time. Your pet gets hungry, "
                + "tired and bored whether the game is open or not, so come back often - "
                + "leave them " + TimeSystem.ABANDON_DAYS
                + " days and they will find a new owner.");

        gameSave.appendLog("NEW GAME | " + playerName + " adopted " + petName);
    }

    //getters and setters
    public Pet getPet() {
        return this.pet;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String consumeUpdateMessage() {
        String message = updateMessage.trim();
        updateMessage = "";
        return message;
    }
    
    public boolean hasSave(){
        return gameSave.exists();
    }
    
    public String getTimeText() {
        return timeSystem.getRealTimeText();
    }
}
