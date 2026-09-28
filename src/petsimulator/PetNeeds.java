/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov, modifications by AJ
 */
public class PetNeeds {
    public final int MAX = 100;
    //At or below this level a need counts as low and the player gets a warning
    public static final int LOW = 20;

    private int energy;
    private int hunger;
    private int happiness;

    public PetNeeds(){
        this.energy = 100;
        this.hunger = 100;
        this.happiness = 100;
    }

    //Used when loading a save, so the pet comes back with the needs it had
    //Anything out of range in the file is clamped back to 0-MAX
    public PetNeeds(int energy, int hunger, int happiness){
        this.energy = clamp(energy);
        this.hunger = clamp(hunger);
        this.happiness = clamp(happiness);
    }

    //Forces a value into the legal 0-MAX range
    private int clamp(int value){
        if (value < 0){
            return 0;
        }
        if (value > MAX){
            return MAX;
        }
        return value;
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
        energy = clamp(energy + num);
    }

    public void changeHunger(int num){
        hunger = clamp(hunger + num);
    }

    public void changeHappiness(int num){
        happiness = clamp(happiness + num);
    }

    public boolean needsSatisfied(){
        return hunger>0 && happiness>0 && energy>0;
    }

    //True if any need has dropped to a level worth warning the player about
    public boolean anyLow(){
        return hunger<=LOW || energy<=LOW || happiness<=LOW;
    }

    //The need the player should deal with first, used for the warning message
    public String worstNeed(){
        if (hunger <= energy && hunger <= happiness){
            return "hungry";
        }
        if (energy <= happiness){
            return "exhausted";
        }
        return "miserable";
    }
}
