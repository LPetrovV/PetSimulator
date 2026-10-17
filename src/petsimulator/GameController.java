/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package petsimulator;

import javax.swing.JButton;
import javax.swing.Timer;
import java.util.function.Supplier;
import java.util.function.Consumer;

/**
 *
 * @author petro
 */
//this class handles interactions from the GUI and updates the model
public class GameController {

    private final Window window;
    private final Game model;
    private final ActionHandler actionHandler;

    private StartScreenGUI startScene;
    private NewGamePromptGUI newGameScene;
    private GameSceneGUI view;
    private GameMessageGUI messageGUI;

    private javax.swing.Timer gameDateTimer;
    private javax.swing.Timer gameUpdateTimer;

    private static final int DATE_TICK_MS = 1000;
    private static final int UPDATE_TICK_MS = 30000;

    private IScene current;

    public GameController(Window window, Game model, ActionHandler actionHandler) {
        this.window = window;
        this.model = model;
        this.actionHandler = actionHandler;
    }

    //on application start display title screen 
    public void start() {
        startScene = show(() -> new StartScreenGUI(window));
        messageGUI = new GameMessageGUI(window);

        boolean saveExists = model.hasSave();
        startScene.setSaveStatus(saveExists ? "Save file found" : "No save file found");
        startScene.setLoadEnabled(saveExists);

        startScene.setOnNewGame(this::openNewGamePromptScene);
        startScene.setOnLoadGame(this::loadGame);

        window.setVisible(true);
    }

    // start new game
    private void openNewGamePromptScene() {
        startScene.remove();
        //open prompt input scene 
        newGameScene = show(() -> new NewGamePromptGUI(window));
        newGameScene.setOnStart(this::checkNewGameRequirements);

        window.setVisible(true);
    }

    public void checkNewGameRequirements() {
        String playerName = newGameScene.getPlayerName();
        String petName = newGameScene.getPetName();
        if (!playerName.equals("") && !petName.equals("")) {
            model.newGame(petName, playerName);
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
        view.setDate(model.getTimeText());// date/time from the TimeSystem, formatted as strings
    }

    // switch to main scene
    private void loadMainScene() {
        view = show(() -> new GameSceneGUI(window));

        attachListeners();
        refreshView();

        //display news messages
        messageGUI.showMessage(model.consumeUpdateMessage());

        startDateTimer();
        startUpdateTimer();

        window.revalidate();
        window.repaint();
    }

    private <T extends IScene> T show(Supplier<T> next) {
        if (current != null) {
            current.remove();        // remove the old scene before building the new one
        }
        T scene = next.get();
        current = scene;
        window.revalidate();
        window.repaint();
        return scene;
    }

    // attach action listeners to key buttons in main scene
    private void attachListeners() {
        bindAction(view::setOnCheckTree, "Check Apple Tree");
        bindAction(view::setOnPutToBed, "Put to Bed");
        view.setOnQuit(this::quit);
        view.setItemUseHandler(this::useItem);
    }

    private void bindAction(Consumer<Runnable> register, String actionName) {
        register.accept(() -> {
            String message = actionHandler.handleAction(actionName, model.getPet(), model.getPlayer());
            messageGUI.showMessage(message, UPDATE_TICK_MS);
            refreshView();
        });
    }

    private void useItem(String itemName) {
        Item item = model.getPlayer().getInventory().findByName(itemName);
        if (item == null) {
            return;
        }
        var message = actionHandler.handleItemAction(item, model.getPet(), model.getPlayer());

        messageGUI.showMessage(message, 3000);
        refreshView();
    }

    private void startDateTimer() {
        gameDateTimer = new javax.swing.Timer(DATE_TICK_MS, e -> dateTick());
        gameDateTimer.start();
    }

    private void dateTick() {
        try {
            view.setDate(model.timeSystem.getRealTimeText());
        } catch (QuitException ex) {
            gameDateTimer.stop();     // pet has left, stop updating
        }

//        refreshView();
    }

    private void startUpdateTimer() {
        gameUpdateTimer = new javax.swing.Timer(UPDATE_TICK_MS, e -> updateTick());
        gameUpdateTimer.start();
    }

    private void updateTick() {
        try {
            model.update();
        } catch (QuitException ex) {
            gameUpdateTimer.stop();     // pet has left, stop updating
            // show the final message
            messageGUI.showMessage(model.consumeUpdateMessage(), 4000);
        }

        String news = model.consumeUpdateMessage();
        if (!news.isEmpty()) {
            messageGUI.showMessage(news, 4000);
        }
        refreshView();
    }

    private void quit() {
        gameDateTimer.stop();
        gameUpdateTimer.stop();
        model.quit();
        window.dispose();
    }
}
