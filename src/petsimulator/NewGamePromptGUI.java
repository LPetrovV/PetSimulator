/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import javax.swing.*;
import java.awt.*;

/**
 * View for the new game screen: a player name field and a pet name field.
 * Contains no game logic: the controller attaches a listener to startButton,
 * reads the names with the getters, and validates them.
 *
 * @author petro
 */
public class NewGamePromptGUI {

    private JFrame frame;
    private JPanel rootPanel;

    private JTextField playerNameField = new JTextField(15);
    private JTextField petNameField = new JTextField(15);
    private JLabel errorLabel = new JLabel(" ");

    public JButton startButton = new JButton("Start");

    private final Color buttonColor = new Color(0x4C9FFF);
    private final Dimension buttonSize = new Dimension(160, 40);

    public NewGamePromptGUI(JFrame frame) {
        this.frame = frame;

        rootPanel = new JPanel();
        rootPanel.setOpaque(false); 
        rootPanel.setLayout(new GridBagLayout()); // centres the content block

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        //title
        JLabel titleLabel = new JLabel("New Game");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 28f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //fields (each label + field pair is its own row)
        JPanel playerRow = createFieldRow("Your name:", playerNameField);
        JPanel petRow = createFieldRow("Pet name:", petNameField);

        //error message (blank by default, set by the controller)
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //button
        startButton.setBackground(buttonColor);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(buttonSize);
        startButton.setMaximumSize(buttonSize);

        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        content.add(playerRow);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(petRow);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(errorLabel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(startButton);

        rootPanel.add(content);
        frame.add(rootPanel, BorderLayout.CENTER);
    }

    //builds one row: a label followed by its text field
    private JPanel createFieldRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.add(new JLabel(labelText));
        row.add(field);
        return row;
    }

    // ------------------------------------------------------------------
    // Getters / setters used by the controller
    // ------------------------------------------------------------------

    public String getPlayerName() {
        return playerNameField.getText().trim();
    }

    public String getPetName() {
        return petNameField.getText().trim();
    }
    
    public void setErrorMessage(String message) {
        errorLabel.setText(message.isEmpty() ? "Please fill out both fields" : message);
    }

    /** Removes this scene from the frame so another scene can be shown. */
    public void remove() {
        frame.remove(rootPanel);
        frame.revalidate();
        frame.repaint();
    }

}