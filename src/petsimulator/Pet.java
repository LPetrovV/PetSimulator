/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public class Pet implements IPetBehaviour{
    private String name;
    private int age;
    private PetNeeds needs;
    
    
    public Pet(String name){
        this.name = name;
        this.age = 0;
        this.needs = new PetNeeds();
    }
    
    @Override
    public void putToBed(){
        needs.changeEnergy(100);
    }
    
    @Override
    public void playGame(ToyItem toy){
        needs.changeHappiness(50);
    }
    
    @Override
    public void feed(FoodItem item){
        needs.changeHunger(item.getBuff());
    }
    
    @Override
    public boolean isAlive(){
        return needs.needsSatisfied();
    }
    
    public void increaseAge(){
        age++;
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
