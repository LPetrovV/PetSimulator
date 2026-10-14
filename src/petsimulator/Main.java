/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public class Main {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ActionHandler handler = new ActionHandler();
            handler.registerAction(new PutToBedAction());
            handler.registerAction(new FeedAction());
            handler.registerAction(new PlayGameAction());
            handler.registerAction(new CheckTreeAction());

            new GameController(new Window(), new Game(), handler).start();
        });
    }
}
