/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public class Item {
    private final String name;
    private final int price;
    
    private final int buff;
    
    public Item(String name, int price, int buff){
        this.name = name;
        this.price = price;
        this.buff = buff;
    }
    
    public String getName(){
        return name;
    }
    
    public int getPrice(){
        return price;
    }
    
    public int getBuff(){
        return buff;
    }
}
