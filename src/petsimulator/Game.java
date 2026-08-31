/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author petro
 */
public class Game {

    Player player;
    Pet pet;
    //SaveSystem gameSave; add savesystem 
    ActionHandler actionHandler = new ActionHandler();
    InputHandler inputHandler = new InputHandler();
    GraphicsHandler draw = new GraphicsHandler();

    public static void main(String[] args) {
        //Instantiate new game
        Game game = new Game();
        
        //Start game + game loop, while quit expection is not caught
        try {
            game.start();
            while (true){
                game.update();
            }
        }
        catch (QuitException e){
            game.quit();
        }
    }

    public void start() {
        //On start, new game or load game
        String gameOptionPrompt = "Game Options:\n1.New Game\n2.Load Save";
        ArrayList<String> gameOptionValidOptions = new ArrayList<>(List.of("1", "2"));
        
        //Collect player input
        String gameOption = inputHandler.collectValidInput(gameOptionPrompt, gameOptionValidOptions);
        
        //If new game selected
        if (gameOption.equals(gameOptionValidOptions.get(0))) {
            //start new game
            newGame();
        } else if (gameOption.equals(gameOptionValidOptions.get(1))) {
            //load save
        }
        
        //Register available actions to actionHandler
        actionHandler.registerAction(new PutToBedAction());
        actionHandler.registerAction(new FeedAction());
        actionHandler.registerAction(new PlayGameAction());
        
        //DELETE LATER - testing player inventory
        for (int i = 0; i < 20; i++) {
            FoodItem apple = new FoodItem("apple", 2, 10);
            player.getInventory().addItem(apple);
        }
    }

    public void update() {
        //display pet stats, pet and items 
        draw.displayPet(pet);

        draw.displayInventory(player.getInventory(), Item.class);

        //display player options
        String chosen = actionHandler.displayPlayerOptions();
        actionHandler.handleAction(chosen, pet, player);
    }

    public void saveGame() {
        //save player and pet stats
        //save system time to keep track of time passed on game load 
    }

    public void loadGame() {
        //load from existing save file
        //if save file doesn't exist, start new game
    }

    public void quit() {
        //save game and quit
        System.out.println("See you next time!");
        saveGame();
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

    }

    
}
