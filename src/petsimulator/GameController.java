/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package petsimulator;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;

/**
 *
 * @author petro
 */
//this class handles interactions from the GUI and updates the model
public class GameController {

    private final Window window;
    private final Game model;
    private final ActionHandler actionHandler;

    private StartScene startScene;
    private NewGamePromptGUI newGameScene;
    private GameSceneGUI view;
    private GameMessageGUI messageGUI;

    public GameController(Window window, Game model, ActionHandler actionHandler) {
        this.window = window;
        this.model = model;
        this.actionHandler = actionHandler;
    }

    //on application start display title screen 
    public void start() {
        startScene = new StartScene(window);

        boolean saveExists = model.gameSave.exists();
        startScene.setSaveStatus(saveExists ? "Save file found" : "No save file found");
        startScene.setLoadEnabled(saveExists);

        startScene.newGameButton.addActionListener(e -> openNewGamePromptScene());
        startScene.loadGameButton.addActionListener(e -> loadGame());

        window.setVisible(true);
    }

    // start new game
    private void openNewGamePromptScene() {
        startScene.remove();
        //open prompt input scene 
        newGameScene = new NewGamePromptGUI(window);

        newGameScene.startButton.addActionListener(e -> checkNewGameRequirements());
        window.setVisible(true);
    }

    public void checkNewGameRequirements() {
        String playerName = newGameScene.getPlayerName();
        String petName = newGameScene.getPetName();
        if (!playerName.equals("") && !petName.equals("")) {
            newGameScene.remove();

            model.newGame(playerName, petName);
            loadMainScene();
        } else {
            newGameScene.setErrorMessage("Please fill out both fields");
        }
    }

    // load save 
    private void loadGame() {
        if (!model.loadGame()) {
            startScene.setSaveStatus("Could not load save file");
            return;
        }
        loadMainScene();
    }

    private void refreshView() {
        Pet pet = model.getPet();
        view.setPetInfo(pet.getName(), pet.getAge());
        PetNeeds n = pet.getNeeds();
        view.setNeeds(n.getHappiness(), n.getHunger(), n.getEnergy()); // adjust to your getter names
        view.setInventory(model.getPlayer().getInventory().getItemsAsMap());   // item name -> quantity, built from the player's inventory
        view.setDate(model.timeSystem.getRealTimeText());// date/time from the TimeSystem, formatted as strings
    }

    // switch to main scene
    private void loadMainScene() {
        startScene.remove();

        view = new GameSceneGUI(window);
        attachListeners();
        refreshView();
        
        //display news messages
        messageGUI = new GameMessageGUI(window);
        messageGUI.showMessage(model.getUpdateMessage());

        window.revalidate();
        window.repaint();
    }

    // attach action listeners to key buttons in main scene
    private void attachListeners() {
        //fixed buttons: button -> action name registered in the ActionHandler
        bindAction(view.checkTreeButton, "Check Apple Tree");
        bindAction(view.putToBedButton, "Put to Bed");

        view.quitButton.addActionListener(e -> quit());

        //inventory "Use" buttons: the view reports the item name, we handle it
        view.setItemUseHandler(this::useItem);
    }

    private void bindAction(JButton button, String actionName) {
        button.addActionListener(e -> {
            var message = actionHandler.handleAction(actionName, model.getPet(), model.getPlayer());
            
            messageGUI = new GameMessageGUI(window);
            messageGUI.showMessage(message, 3000);
            
            refreshView();
        });
    }

    private void useItem(String itemName) {
        Item item = model.getPlayer().getInventory().findByName(itemName);
        if (item == null) {
            return;
        }
        var message = actionHandler.handleItemAction(item, model.getPet(), model.getPlayer());
        
        messageGUI = new GameMessageGUI(window);
        messageGUI.showMessage(message, 3000);
        refreshView();
    }

    private void quit() {
        model.quit();
        window.dispose();
    }
}
