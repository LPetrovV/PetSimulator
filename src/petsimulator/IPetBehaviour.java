/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public interface IPetBehaviour {
    public void putToBed();
    public void playGame(ToyItem item);
    public void feed(FoodItem item);
    public boolean isAlive();
}
