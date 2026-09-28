/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 *
 * @author Lisa Petrov
 */
//This method handles action selection and pet interaction
public class ActionHandler {
    private final LinkedHashMap<String, IAction> actions = new LinkedHashMap<>();
    private final InputHandler inputHandler = new InputHandler();
    private final GraphicsHandler draw = new GraphicsHandler();
    
    //Add action to register
    public void registerAction(IAction action) {
        actions.put(action.getName(), action);
    }
    
    //Displays available action options for player
    public String displayPlayerOptions() {
        ArrayList<String> optionNames = new ArrayList<>(actions.keySet());
        ArrayList<String> validNumbers = new ArrayList<>();

        StringBuilder prompt = new StringBuilder("What do you want to do?\nType 'quit' to exit game.\n\n");
        for (int i = 0; i < optionNames.size(); i++) {
            int number = i + 1;
            prompt.append(number).append(". ").append(optionNames.get(i)).append("\n");
            validNumbers.add(String.valueOf(number));
        }

        String chosenNumber = inputHandler.collectValidInput(prompt.toString(), validNumbers);
        return optionNames.get(Integer.parseInt(chosenNumber) - 1);
    }
    
    //Handles action after player select 
    public void handleAction(String input, Pet pet, Player player) {
        //gets appropriate action
        IAction action = actions.get(input);
        if (action == null) {
            System.out.println("Unknown action: " + input);
            return;
        }
        
        //Get user to select item if action requires item
        if (action.requiresItem()) {
            Item chosen = selectItem(player, action.getItemType());
            if (chosen != null && player.getInventory().hasItem(chosen)) {
                //Food is eaten and gone, a toy stays in the bag for next time
                if (chosen.isConsumed()) {
                    player.getInventory().removeItem(chosen);
                }
                action.execute(pet, player, chosen);
            } else {
                System.out.println("You don't have that.");
            }
        //If no item required, execute action
        } else {
            action.execute(pet, player, null);
        }
    }
    
    //Handles player selection of given item type
    private Item selectItem(Player player, Class<? extends Item> type) {
        System.out.println("Use which item?");
        draw.displayInventory(player.getInventory(), type);
        String chosenName = inputHandler.collectUserInput();
        return player.getInventory().findByName(chosenName);
    }
}