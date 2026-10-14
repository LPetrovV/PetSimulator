/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
import javax.swing.*;
import java.awt.Color;
import java.awt.BorderLayout;

public class Window extends JFrame{
    
    
    Color bgColor = new Color(0xFFF3E5);
    
    public Window(){
        this.setTitle("Virtual Pet Simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setResizable(false);
        this.setSize(800, 550);
        this.setLayout(new BorderLayout());
        //this.setVisible(true);
        
        //changing background colour
        this.getContentPane().setBackground(bgColor);
    }
}
