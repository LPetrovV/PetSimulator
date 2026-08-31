/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public interface IAction {
    public void execute(Pet pet, Item item);
    public String getName();
    boolean requiresItem();
    public Class<? extends Item> getItemType();
}
