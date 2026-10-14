/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov
 */
public class FeedAction implements IAction {
    private String message = "";
    
    @Override
    public void execute(Pet pet, Player player, Item item) {
        message = "";
        // if item is a foodItem, feed pet, display message
        if (item instanceof FoodItem food) {
            pet.feed(food);
            message+=(pet.getName()+" ate "+item.getName()+"! Plus "+item.getBuff()+" to hunger!\n");
        }
    }
    @Override
    public String getName() { 
        return "Feed"; 
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public boolean requiresItem() { 
        return true; 
    }
    
    @Override
    public Class<? extends Item> getItemType(){
        return FoodItem.class;
    }
}
