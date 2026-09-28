/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lisa Petrov, modifications by AJ
 */
public class Game {

    Player player;
    Pet pet;
    SaveSystem gameSave = new SaveSystem();
    TimeSystem timeSystem = new TimeSystem();
    ActionHandler actionHandler = new ActionHandler();
    InputHandler inputHandler = new InputHandler();
    GraphicsHandler draw = new GraphicsHandler();

    public static void main(String[] args) {
        //Instantiate new game
        Game game = new Game();

        //Start game + game loop, while quit expection is not caught
        try {
            game.start();
            while (true) {
                game.update();
            }
        } catch (QuitException e) {
            game.quit();
        }
    }

    public void start() {
        //On start, new game or load game
        String gameOptionPrompt = "Game Options:\n1.New Game\n2.Load Save";
        ArrayList<String> gameOptionValidOptions = new ArrayList<>(List.of("1", "2"));

        //Tell the player up front whether there is anything to load
        if (gameSave.exists()) {
            System.out.println("A saved game was found in " + SaveSystem.SAVE_FILE);
        } else {
            System.out.println("No save file found yet - choose New Game.");
        }

        //Collect player input
        String gameOption = inputHandler.collectValidInput(gameOptionPrompt, gameOptionValidOptions);

        //If new game selected
        if (gameOption.equals(gameOptionValidOptions.get(0))) {
            //start new game
            newGame();
        } else if (gameOption.equals(gameOptionValidOptions.get(1))) {
            //load save, and fall back to a new game if there isn't a usable one
            if (!loadGame()) {
                System.out.println("Starting a new game instead.");
                newGame();
            }
        }

        //Register available actions to actionHandler
        actionHandler.registerAction(new PutToBedAction());
        actionHandler.registerAction(new FeedAction());
        actionHandler.registerAction(new PlayGameAction());
        actionHandler.registerAction(new CheckTreeAction());
    }

    public void update() {
        //If the pet is gone there is nothing left to do
        //Bring the game up to date with the real world clock first, so the
        //needs on screen are always what they should be right now
        String news = timeSystem.update(pet, player.getGarden());
        if (!news.isEmpty()) {
            System.out.println("\n" + news);
        }

        //If the pet is gone there is nothing left to do
        if (!pet.isAlive()) {
            System.out.println("\n" + pet.getName() + " is no longer with you.");
            System.out.println("You kept them going for " + pet.getAge() + " day(s).");
            throw new QuitException();
        }

        //display the real time, pet stats, pet and items
        draw.displayTime(timeSystem, player.getGarden());
        draw.displayPet(pet);

        draw.displayInventory(player.getInventory(), Item.class);

        //Warn the player while there is still time to do something about it
        if (pet.getNeeds().anyLow()) {
            System.out.println("WARNING: " + pet.getName() + " is "
                    + pet.getNeeds().worstNeed() + "! Look after them soon.");
        }

        //display player options
        String chosen = actionHandler.displayPlayerOptions();
        actionHandler.handleAction(chosen, pet, player);
    }

    public void saveGame() {
        //save player and pet stats
        //SaveData also stores the system time, so the next load can work out
        //how long the player has been away
        SaveData data = new SaveData(player, pet, timeSystem);

        if (gameSave.saveToFile(data)) {
            System.out.println("Game saved to " + SaveSystem.SAVE_FILE);
            gameSave.appendLog("SAVE | " + player.getName() + " saved "
                    + pet.getName() + " (age " + pet.getAge() + ")");
        }
    }

    public boolean loadGame() {
        //load from existing save file
        SaveData data = gameSave.loadSave();

        //if save file doesn't exist or can't be read, the caller starts a new game
        if (data == null) {
            System.out.println("There is no save file to load.");
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

        System.out.println("\nWelcome back, " + player.getName() + "!");

        //Apply all the real time that passed while the game was closed
        String news = timeSystem.update(pet, player.getGarden());
        if (!news.isEmpty()) {
            System.out.println(news);
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
        //save game and quit
        System.out.println("See you next time!");

        if (pet != null && pet.isAlive()) {
            saveGame();
        } else if (pet != null) {
            //the pet has left, so there is nothing worth coming back to
            gameSave.deleteSave();
            gameSave.appendLog("LOST | " + pet.getName() + " left after "
                    + pet.getAge() + " day(s)");
            System.out.println("The save file has been cleared - "
                    + "next time you will start with a new pet.");
        }
    }

    public void newGame() {
        //Collect player name
        String playerName = inputHandler.collectValidType("What is your name?", s -> {
            if (s.isBlank()) {
                throw new IllegalArgumentException("Name can't be empty");
            }
            return s;
        });
        //Create new player
        this.player = new Player(playerName);

        //implement choosing a pet option - DO LATER

        //Collect pet name
        String petName = inputHandler.collectValidType("Name your pet:", s -> {
            if (s.isBlank()) {
                throw new IllegalArgumentException("Name can't be empty");
            }
            return s;
        });

        //Create new pet
        //change this to suit specific pet options
        this.pet = new Pet(petName);

        //Starting items are read from data/items.txt instead of being hard coded
        for (Item item : gameSave.loadItems()) {
            player.getInventory().addItem(item);
        }

        //A new pet starts the clock from right now
        this.timeSystem = new TimeSystem();

        System.out.println("\nThere is an apple tree in your garden. Check it often - "
                + "it grows one apple every " + TimeSystem.MINUTES_PER_APPLE
                + " real minutes, up to " + AppleTree.MAX_APPLES + " at a time.");
        System.out.println("This game runs on real world time. Your pet gets hungry, "
                + "tired and bored whether the game is open or not, so come back often - "
                + "leave them " + TimeSystem.ABANDON_DAYS
                + " days and they will find a new owner.");

        gameSave.appendLog("NEW GAME | " + playerName + " adopted " + petName);
    }

}
