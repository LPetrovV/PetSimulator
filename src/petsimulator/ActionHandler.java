/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import java.util.LinkedHashMap;

/**
 *
 * @author Lisa Petrov
 */
//This method handles action selection and pet interaction
public class ActionHandler {

    private final LinkedHashMap<String, IAction> actions = new LinkedHashMap<>();

    //Add action to register
    public void registerAction(IAction action) {
        actions.put(action.getName(), action);
    }

    
    //Handles action after player select 
    public String handleAction(String input, Pet pet, Player player) {
        //gets appropriate action
        IAction action = actions.get(input);
        if (action == null) {
            return "Unknown action: " + input;
        }

        action.execute(pet, player, null);
        return action.getMessage();
    }

    //Handle use item action
    public String handleItemAction(Item item, Pet pet, Player player) {
        IAction action = null;
        if (item instanceof FoodItem) {
            action = actions.get("Feed");
        } else if (item instanceof ToyItem) {
            action = actions.get("Play Game");
        }

        if (action == null) {
            return "Unknown action: ";
        }
        if (item.isConsumed()) {
            player.getInventory().removeItem(item);
        }
        action.execute(pet, player, item);
        return action.getMessage();
    }
}
