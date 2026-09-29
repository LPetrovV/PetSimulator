/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author petro
 */
public class ItemComponent extends JPanel{
    //delete later - make parameter just item
    private int numInstances;
    private String name;
    
    private JLabel instancesLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    
    public JButton useButton;
    
    private final Color buttonColor = new Color(0x4C9FFF);
    
    //Item item; implement later to use given item
    public ItemComponent(String name, int numInstances){
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setOpaque(false);
        nameLabel.setText(name);
        instancesLabel.setText("x"+Integer.toString(numInstances));
        
        useButton = new JButton("Use");
        useButton.setBackground(buttonColor);
        
        this.add(instancesLabel);
        this.add(nameLabel);
        this.add(useButton);
    }
    
    public void updateInstances(int numInstances){
        instancesLabel.setText("x"+Integer.toString(numInstances));
    }
    
    public int getNumInstances(){
        return this.numInstances;
    }
    
    public String getName(){
        return this.name;
    }
}
