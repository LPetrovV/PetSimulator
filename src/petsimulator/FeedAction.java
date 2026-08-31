/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public class FeedAction implements IAction {
    @Override
    public void execute(Pet pet, Item item) {
        // if item is a foodItem, feed pet, display message
        if (item instanceof FoodItem food) {
            pet.feed(food);
            System.out.println(pet.getName()+" ate "+item.getName()+"! Plus "+item.getBuff()+" to hunger!");
        }
    }
    @Override
    public String getName() { 
        return "Feed"; 
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
