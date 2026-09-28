/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov
 */
public class PlayGameAction implements IAction {
    @Override
    public void execute(Pet pet, Player player, Item item) {
        if (item instanceof ToyItem toy) {
            pet.playGame(toy);
            System.out.println(pet.getName()+" is playing! Plus "+item.getBuff()+" to happiness!\nThey feel a bit tired now");
        }
        else {
            System.out.println("That's not a toy :(");
        }
    }
    @Override
    public String getName() { 
        return "Play Game"; 
    }
    @Override
    public boolean requiresItem() { 
        return true; 
    }
    
    @Override
    public Class<? extends Item> getItemType(){
        return ToyItem.class;
    }
}
