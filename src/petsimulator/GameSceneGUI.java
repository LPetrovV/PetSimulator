/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.awt.Color;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 *
 * @author petro
 */
public class GameSceneGUI {

    private JPanel statsPanel;
    private JPanel infoPanel;
    private ImagePanel mainPanel;
    private JFrame frame;

    //sizing
    private int infoWidth = 200;
    private int statsHeight = 75;

    //initialise with controller
    public JPanel happinessComponent;
    public JPanel energyComponent;
    public JPanel hungerComponent;

    public JLabel dateLabel = new JLabel();
    public JLabel timeLabel = new JLabel();

    //inventory
    public JPanel inventoryPanel = new JPanel();

    //pet labels
    private JLabel petNameLabel = new JLabel();
    private JLabel petAgeLabel = new JLabel();

    //action buttons
    public JButton checkTreeButton = new JButton("Check Apple Tree");
    public JButton putToBedButton = new JButton("Put To Bed");
    
    private final Color UIBgColor = new Color(0xDFF0FF);
    private final Color buttonColor = new Color(0x4C9FFF);

    public GameSceneGUI(JFrame frame) {
        this.frame = frame;

        Image bgImage = null;
        URL bgURL = GameSceneGUI.class.getResource("/resources/background.jpg");
        if (bgURL != null) {
            bgImage = new ImageIcon(bgURL).getImage();
        } else {
            System.err.println("Could not find background image!");
        }

        //Panel layout
        statsPanel = new JPanel();
        infoPanel = new JPanel();
        mainPanel = new ImagePanel(bgImage);

        //panel bg colours
        statsPanel.setBackground(UIBgColor);
        infoPanel.setBackground(UIBgColor);

        //changing sizes
        statsPanel.setPreferredSize(new Dimension(0, statsHeight));
        infoPanel.setPreferredSize(new Dimension(infoWidth, 0));

        //adding margins
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, infoWidth));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //adding panels to frame
        frame.add(statsPanel, BorderLayout.NORTH);
        frame.add(infoPanel, BorderLayout.EAST);
        frame.add(mainPanel, BorderLayout.CENTER);

        statsPanelSetup();
        infoPanelSetup();
        mainPanelSetup();
    }

    public void statsPanelSetup() {
        //stats layout
        statsPanel.setLayout(new GridLayout(1, 4, 10, 0));

        //initialise needs components here
        statsPanel.add(new NeedsComponent("Happiness", 99, 100));
        statsPanel.add(new NeedsComponent("Hunger", 99, 100));
        statsPanel.add(new NeedsComponent("Energy", 99, 100));
    }

    public void infoPanelSetup() {
        //info layout
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        //delete later - change with update
        dateLabel.setText("Tue 7th September");
        timeLabel.setText("11:13");

        //inventory label
        JLabel inventoryLabel = new JLabel();
        inventoryLabel.setText("Inventory");

        //fixing alignment for all components 
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inventoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inventoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        //adding components to infoPanel
        infoPanel.add(dateLabel);
        infoPanel.add(timeLabel);
        //spacing
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(inventoryLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        inventoryPanel.setOpaque(false);
        
        //in controller, create itemComponent for each item in inventory
        inventoryPanel.add(new ItemComponent("Apple", 3));
        inventoryPanel.add(new ItemComponent("Apple", 3));
        inventoryPanel.add(new ItemComponent("Apple", 3));
        
        
        infoPanel.add(inventoryPanel);
    }

    public void mainPanelSetup() {
        mainPanel.setLayout(new BorderLayout());

        // holds everything that should sit at the top
        JPanel topContent = new JPanel();
        topContent.setLayout(new BoxLayout(topContent, BoxLayout.Y_AXIS));
        topContent.setOpaque(false); // let the background show through

        //pet info panel
        JPanel petInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        petInfoPanel.setOpaque(false);
        setPetInfo("Bob", 3);
        petInfoPanel.add(petNameLabel);
        petInfoPanel.add(petAgeLabel);

        //action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        actionPanel.setOpaque(false);
        
        checkTreeButton.setBackground(buttonColor);
        putToBedButton.setBackground(buttonColor);
        actionPanel.add(checkTreeButton);
        actionPanel.add(putToBedButton);

        topContent.add(petInfoPanel);
        topContent.add(actionPanel);

        mainPanel.add(topContent, BorderLayout.NORTH);
        
        //pet icon
        URL imageURL = GameSceneGUI.class.getResource("/resources/catIcon.png");

        if (imageURL != null) {
            ImageIcon imageIcon = new ImageIcon(imageURL);
            JLabel label = new JLabel(imageIcon);
            mainPanel.add(label);
        } else {
            System.err.println("Could not find the image file!");
        }
    }

    public void setPetInfo(String name, int age) {
        petNameLabel.setText(name + " - ");
        petAgeLabel.setText(age + " days old");
    }

}
