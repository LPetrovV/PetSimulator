/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov, modifications by AJ
 */
public class Pet implements IPetBehaviour{
    private String name;
    private int age;
    private PetNeeds needs;
    //True once the pet has given up waiting and gone to a new owner
    private boolean hasLeft;
    
    
    public Pet(String name){
        this.name = name;
        this.age = 0;
        this.needs = new PetNeeds();
        this.hasLeft = false;
    }
    
    //Used when loading a save, so the pet comes back exactly as it was left
    public Pet(String name, int age, PetNeeds needs){
        this.name = name;
        this.age = age;
        this.needs = needs;
        this.hasLeft = false;
    }
    
    @Override
    public void putToBed(){
        needs.changeEnergy(100);
    }
    
    @Override
    public void playGame(ToyItem toy){
        needs.changeHappiness(toy.getBuff());
    }
    
    @Override
    public void feed(FoodItem item){
        needs.changeHunger(item.getBuff());
    }
    
    @Override
    public boolean isAlive(){
        return !hasLeft && needs.needsSatisfied();
    }
    
    //Called by TimeSystem when the player has been away too long
    public void leave(){
        hasLeft = true;
    }
    
    public boolean hasLeft(){
        return hasLeft;
    }
    
    public void increaseAge(){
        age++;
    }
    
    //The age is worked out from the real world clock, so TimeSystem sets it
    //rather than counting it up itself
    public void setAge(int age){
        this.age = Math.max(0, age);
    }
    
    public String getName(){
        return name;
    }
    
    public int getAge(){
        return age;
    }
    
    public PetNeeds getNeeds(){
        return needs;
    }
}
