/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov
 */
public interface IAction {
    //The player is passed in as well as the pet, so an action can also reach
    //the inventory, the coins and the garden - not just the pet itself
    public void execute(Pet pet, Player player, Item item);
    public String getName();
    boolean requiresItem();
    public Class<? extends Item> getItemType();
}
