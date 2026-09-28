/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author Lisa Petrov 
 */
public class Player {
    private String name;
    private int coins;
    private Inventory inventory;
    //The player owns the garden, so any action that gets given the player can
    //reach the apple tree
    private AppleTree garden;
    
    public Player(String name){
        this(name, 0);
    }
    
    //Used when loading a save, so the player keeps the coins they had
    public Player(String name, int coins){
        this(name, coins, new AppleTree());
    }
    
    //Used when loading a save, so the tree comes back as it was left
    public Player(String name, int coins, AppleTree garden){
        this.name = name;
        this.coins = coins;
        this.inventory = new Inventory();
        this.garden = garden;
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
    
    public AppleTree getGarden(){
        return garden;
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
