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
    
    private String message = "";
    
    @Override
    public void execute(Pet pet, Player player, Item item) {
        AppleTree tree = player.getGarden();
        
        message = "";
        message+=("Apples on the tree: " + tree.getApples() + "/" + AppleTree.MAX_APPLES+"\n");

        //Nothing ready yet - tell the player how long to wait
        if (tree.getApples() == 0) {
            message+=("Nothing ripe yet. The next apple needs about "
                    + tree.minutesUntilNextApple() + " more real minute(s) to grow.\n");
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

        message+=("You picked " + picked + " apple(s) and put them in your bag.\n");
        message+=("The tree will start growing more straight away.");
    }
    
    @Override
    public String getMessage(){
        return message;
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
