/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov 
 */
public class ToyItem extends Item{
    public ToyItem(String name, int price, int buff){
        super(name, price, buff);
    }
    
    //A toy is not eaten, so it stays in the bag and can be played with again
    @Override
    public boolean isConsumed(){
        return false;
    }
}
