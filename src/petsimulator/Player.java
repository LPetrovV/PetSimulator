/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
public class Player {
    private String name;
    private int coins;
    private Inventory inventory;
    
    public Player(String name){
        this.name = name;
        this.coins = 0;
        this.inventory = new Inventory();
    }
    
    public String getName(){
        return name;
    }
    
    public int getCoins(){
        return coins;
    }
    
    public Inventory getInventory(){
        return inventory;
    }
    
    public void addCoins(int num){
        coins+=num;
    }
    
    public boolean spendCoins(int num){
        if (num <= coins){
            coins-=num;
            return true;
        }
        return false;
    }
}
