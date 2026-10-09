/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package petsimulator;

import java.util.Scanner;

/**
 *
 * @author petro
 */

//this class handles interactions from the GUI and updates the model
public class GameController {
    
    Window window = new Window();
    ActionHandler actionHandler = new ActionHandler();
    StartScene startScene;
    GameSceneGUI view;
    Game model = new Game();

    public GameController() {
        startGame();
    }

    //on application start display title screen 
    private void startGame() {
        startScene = new StartScene(window);
        window.setVisible(true);

        startScene.setLoadEnabled(model.gameSave.exists());

        startScene.newGameButton.addActionListener(e -> newGame());
        startScene.loadGameButton.addActionListener(e -> loadGame());
    }
    
    // start new game
    private void newGame() {
        model.newGame();

        loadMainScene();
    }
    
    // load save 
    private void loadGame() {
        model.loadGame();

        loadMainScene();
    }
    
    // switch to main scene
    private void loadMainScene() {
        startScene.remove();

        view = new GameSceneGUI(window);

        registerActions();
        attachListeners();

        window.setVisible(true);
    }
    
    // attach action listeners to key buttons in main scene
    private void attachListeners() {

        view.checkTreeButton.addActionListener(e -> actionHandler.handleAction("Check Apple Tree", model.pet, model.player));
        view.putToBedButton.addActionListener(e -> actionHandler.handleAction("Put to Bed", model.pet, model.player));
        view.quitButton.addActionListener(e -> model.quit());

    }
    
    // register game actions in ActionHandler
    private void registerActions() {
        actionHandler.registerAction(new PutToBedAction());
        actionHandler.registerAction(new FeedAction());
        actionHandler.registerAction(new PlayGameAction());
        actionHandler.registerAction(new CheckTreeAction());
    }

    public static void main(String[] args) {
        GameController gameController = new GameController();
    }
}
