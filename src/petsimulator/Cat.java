/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov
 */
public class Cat extends Pet{
    
    public Cat(String name){
        super(name);
    }
    
    //Used when loading a save
    public Cat(String name, int age, PetNeeds needs){
        super(name, age, needs);
    }
}
