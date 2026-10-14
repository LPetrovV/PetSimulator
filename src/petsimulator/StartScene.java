/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import javax.swing.*;
import java.awt.*;

/**
 * View for the start screen. Contains no game logic: the controller attaches
 * listeners to the buttons and sets the save status text.
 *
 * @author petro
 */
public class StartScene {

    private JFrame frame;
    private JPanel rootPanel;

    private JLabel titleLabel = new JLabel("Virtual Pet Sim");
    private JLabel saveStatusLabel = new JLabel();

    public JButton newGameButton = new JButton("New Game");
    public JButton loadGameButton = new JButton("Load Game");

    private final Color buttonColor = new Color(0x4C9FFF);
    private final Dimension buttonSize = new Dimension(160, 40);

    public StartScene(JFrame frame) {
        this.frame = frame;

        rootPanel = new JPanel();
        rootPanel.setOpaque(false); // let the frame's background colour show through
        rootPanel.setLayout(new GridBagLayout()); // centres the content block

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        //title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 36f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //save status
        saveStatusLabel.setFont(saveStatusLabel.getFont().deriveFont(16f));
        saveStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setSaveStatus("Checking for save file...");

        //buttons
        styleButton(newGameButton);
        styleButton(loadGameButton);

        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(saveStatusLabel);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(newGameButton);
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(loadGameButton);

        rootPanel.add(content);
        frame.add(rootPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setBackground(buttonColor);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
    }

    // ------------------------------------------------------------------
    // Setters used by the controller
    // ------------------------------------------------------------------

    /** Text such as "Save file found" or "No save file found". */
    public void setSaveStatus(String status) {
        saveStatusLabel.setText(status);
    }

    /** Disable the load button when there is no save file to load. */
    public void setLoadEnabled(boolean enabled) {
        loadGameButton.setEnabled(enabled);
    }

    /** Removes this scene from the frame so another scene can be shown. */
    public void remove() {
        frame.remove(rootPanel);
        frame.revalidate();
        frame.repaint();
    }

}