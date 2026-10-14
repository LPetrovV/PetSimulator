/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.awt.Color;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author petro
 */
public class GameSceneGUI {

    private final JPanel statsPanel;
    private final JPanel infoPanel;
    private final ImagePanel mainPanel;
    private final JFrame frame;

    //sizing
    private final int infoWidth = 200;
    private final int statsHeight = 75;

    //needs components (kept so the controller can update them via the setters below)
    private NeedsComponent happinessComponent;
    private NeedsComponent hungerComponent;
    private NeedsComponent energyComponent;

    private final int defaultMaxNeed = 100;

    public JLabel dateLabel = new JLabel();
    

    //inventory
    public JPanel inventoryPanel = new JPanel();
    //called with the item name when the user clicks "Use" on an item
    private Consumer<String> itemUseHandler;

    //pet labels
    private final JLabel petNameLabel = new JLabel();
    private final JLabel petAgeLabel = new JLabel();

    //action buttons
    public JButton checkTreeButton = new JButton("Check Apple Tree");
    public JButton putToBedButton = new JButton("Put To Bed");

    public JButton quitButton = new JButton("Save & Quit");

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

        //create and keep references to the needs components
        happinessComponent = new NeedsComponent("Happiness", defaultMaxNeed, defaultMaxNeed);
        hungerComponent = new NeedsComponent("Hunger", defaultMaxNeed, defaultMaxNeed);
        energyComponent = new NeedsComponent("Energy", defaultMaxNeed, defaultMaxNeed);

        statsPanel.add(happinessComponent);
        statsPanel.add(hungerComponent);
        statsPanel.add(energyComponent);
    }

    public void infoPanelSetup() {
        //info layout
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        

        //inventory label
        JLabel inventoryLabel = new JLabel();
        inventoryLabel.setText("Inventory");

        //fixing alignment for all components 
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inventoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inventoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        //adding components to infoPanel
        infoPanel.add(dateLabel);
        //spacing
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(inventoryLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        inventoryPanel.setOpaque(false);

        //placeholder items until the controller calls setInventory
        inventoryPanel.add(new ItemComponent("Apple", 3));
        inventoryPanel.add(new ItemComponent("Apple", 3));
        inventoryPanel.add(new ItemComponent("Apple", 3));

        infoPanel.add(inventoryPanel);

        //save and quit button 
        quitButton.setBackground(buttonColor);
        infoPanel.add(quitButton);
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

    // ------------------------------------------------------------------
    // Setters used by the controller to update the view
    // ------------------------------------------------------------------

    public void setPetInfo(String name, int age) {
        petNameLabel.setText(name + " - ");
        petAgeLabel.setText(age + " days old");
    }

    public void setHappiness(int value) {
        happinessComponent.setValue(value);
    }

    public void setHunger(int value) {
        hungerComponent.setValue(value);
    }

    public void setEnergy(int value) {
        energyComponent.setValue(value);
    }

    //convenience method to update all needs in one call
    public void setNeeds(int happiness, int hunger, int energy) {
        setHappiness(happiness);
        setHunger(hunger);
        setEnergy(energy);
    }

    public void setDate(String date) {
        dateLabel.setText(date);
    }

    /**
     * Registers what should happen when the user clicks "Use" on any item.
     * The handler receives the name of the item. Call this before setInventory.
     */
    public void setItemUseHandler(Consumer<String> handler) {
        this.itemUseHandler = handler;
    }

    /**
     * Rebuilds the inventory display from item name -> quantity.
     * Use a LinkedHashMap to keep a stable display order.
     * Items with a quantity of 0 or less are not shown.
     */
    public void setInventory(Map<String, Integer> items) {
        inventoryPanel.removeAll();

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();

            if (quantity <= 0) {
                continue;
            }

            ItemComponent itemComponent = new ItemComponent(itemName, quantity);
            itemComponent.useButton.addActionListener(e -> {
                if (itemUseHandler != null) {
                    itemUseHandler.accept(itemName);
                }
            });
            inventoryPanel.add(itemComponent);
        }

        //Swing doesn't redraw automatically after adding/removing components
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

}