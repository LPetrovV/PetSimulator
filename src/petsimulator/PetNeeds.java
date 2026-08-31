/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public class PetNeeds {
    public final int MAX = 100;
    
    private int energy;
    private int hunger;
    private int happiness;
    
    public PetNeeds(){
        this.energy = 100;
        this.hunger = 100;
        this.happiness = 100;
    }
    
    public int getEnergy(){
        return energy;
    }
    
    public int getHunger(){
        return hunger;
    }
    
    public int getHappiness() {
        return happiness;
    }
    
    public void changeEnergy(int num){
        energy += num;
        if (energy<0){
            energy = 0;
        }
        if (energy>MAX){
            energy = MAX;
        }
    }
    
    public void changeHunger(int num){
        hunger += num;
        if (hunger<0){
            hunger = 0;
        }
        if (hunger>MAX){
            hunger = MAX;
        }
    }
    
    public void changeHappiness(int num){
        happiness += num;
        if (happiness<0){
            happiness = 0;
        }
        if (happiness>MAX){
            happiness = MAX;
        }
    }
    
    public boolean needsSatisfied(){
        return hunger>0 && happiness>0 && energy>0;
    }
}
