/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author AJ Franchuk
 */
//The apple tree in the player's garden - the free source of food.
//The tree grows on the real world clock: every TimeSystem.MINUTES_PER_APPLE
//real minutes one apple appears, and it never holds more than MAX_APPLES at
//once. That cap is what stops the player from stockpiling food and never
//having to think about it - come back to a tree that has been full for two
//days and there are still only five apples waiting.
public class AppleTree {

    public static final int MAX_APPLES = 5;

    //Apples currently hanging on the tree, ready to be picked
    private int apples;
    //Real minutes counted so far towards the next apple
    private int growth;

    //A young tree with a couple of apples already on it
    public AppleTree() {
        this(2, 0);
    }

    //Used when loading a save
    public AppleTree(int apples, int growth) {
        this.apples = clamp(apples);
        this.growth = Math.max(0, growth);
    }

    //Keeps the apple count inside 0 to MAX_APPLES
    private int clamp(int value) {
        if (value < 0) {
            return 0;
        }
        if (value > MAX_APPLES) {
            return MAX_APPLES;
        }
        return value;
    }

    public int getApples() {
        return apples;
    }

    public int getGrowth() {
        return growth;
    }

    public boolean isFull() {
        return apples >= MAX_APPLES;
    }

    //Real minutes still needed before the next apple appears
    public int minutesUntilNextApple() {
        if (isFull()) {
            return 0;
        }
        return TimeSystem.MINUTES_PER_APPLE - (growth % TimeSystem.MINUTES_PER_APPLE);
    }

    //Moves the tree forward by the given number of real minutes
    //Returns how many new apples grew
    public int grow(int minutes) {
        if (minutes <= 0) {
            return 0;
        }

        //A full tree stops counting - it has nowhere to put more fruit
        if (isFull()) {
            growth = 0;
            return 0;
        }

        growth += minutes;
        int grown = 0;

        while (growth >= TimeSystem.MINUTES_PER_APPLE && !isFull()) {
            growth -= TimeSystem.MINUTES_PER_APPLE;
            apples++;
            grown++;
        }

        if (isFull()) {
            growth = 0;
        }
        return grown;
    }

    //Takes one apple off the tree
    //Returns the apple as a FoodItem, or null if the tree is bare
    public FoodItem pickApple() {
        if (apples <= 0) {
            return null;
        }
        apples--;
        return new FoodItem("apple", 2, 10);
    }

    //Draws the tree, showing the apples that are on it as o and empty spots as *
    public String draw() {
        StringBuilder canopy = new StringBuilder();
        for (int i = 0; i < MAX_APPLES; i++) {
            canopy.append(i < apples ? " o" : " *");
        }

        return "\n        (" + canopy.toString().trim() + ")\n"
                + "            ||\n"
                + "            ||\n"
                + "       ~~~~~~~~~~~~~";
    }

    @Override
    public String toString() {
        return apples + "/" + MAX_APPLES + " apples";
    }
}
