/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov, modifications by AJ
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
    
    //Most items are used up when they are used. A subclass that should survive
    //being used - a toy, for example - overrides this and returns false.
    public boolean isConsumed(){
        return true;
    }
}
