/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov
 */
public class PutToBedAction implements IAction {
    @Override
    public void execute(Pet pet, Player player, Item item) {
        pet.putToBed(); // item unused
        System.out.println(pet.getName()+" is sleeping!");
    }
    @Override
    public String getName() { 
        return "Put to Bed"; 
    }
    @Override
    public boolean requiresItem() { 
        return false; 
    }
    
    @Override
    public Class<? extends Item> getItemType(){
        return null;
    }
}
