/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

/**
 *
 * @author petro
 */
//Lets the player go and look at the apple tree in their garden.
//Any apples that have grown since last time are picked and go straight into
//the inventory, ready to be used by the Feed action. This is where all the
//free food in the game comes from, which is why the tree is capped at five
//apples - the player has to keep coming back rather than stockpiling.
public class CheckTreeAction implements IAction {

    @Override
    public void execute(Pet pet, Player player, Item item) {
        AppleTree tree = player.getGarden();

        System.out.println(tree.draw());
        System.out.println();
        System.out.println("Apples on the tree: " + tree.getApples() + "/" + AppleTree.MAX_APPLES);

        //Nothing ready yet - tell the player how long to wait
        if (tree.getApples() == 0) {
            System.out.println("Nothing ripe yet. The next apple needs about "
                    + tree.minutesUntilNextApple() + " more real minute(s) to grow.");
            return;
        }

        //Pick everything that is ready and put it in the bag
        int picked = 0;
        FoodItem apple = tree.pickApple();
        while (apple != null) {
            player.getInventory().addItem(apple);
            picked++;
            apple = tree.pickApple();
        }

        System.out.println("You picked " + picked + " apple(s) and put them in your bag.");
        System.out.println("The tree will start growing more straight away.");
    }

    @Override
    public String getName() {
        return "Check Apple Tree";
    }

    @Override
    public boolean requiresItem() {
        return false;
    }

    @Override
    public Class<? extends Item> getItemType() {
        return null;
    }
}
