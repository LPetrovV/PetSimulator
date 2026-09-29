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
public class NeedsComponent extends JPanel{
    JLabel nameLabel;
    JLabel statsLabel;
    JLabel maxLabel;
    
    private final Color needsColor = new Color(0x428300);
    
    public NeedsComponent(String name, int statsNum, int max){
        
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        nameLabel = new JLabel();
        nameLabel.setText(name+": ");
        nameLabel.setForeground(Color.WHITE);
        
        statsLabel = new JLabel();
        statsLabel.setText(Integer.toString(statsNum));
        statsLabel.setForeground(Color.WHITE);
        
        maxLabel = new JLabel();
        maxLabel.setText("/"+Integer.toString(max));
        maxLabel.setForeground(Color.WHITE);
        
        this.setBackground(needsColor);
        
        this.add(nameLabel);
        this.add(statsLabel);
        this.add(maxLabel);
    }
}
