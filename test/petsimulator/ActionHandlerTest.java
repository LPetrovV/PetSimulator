/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package petsimulator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author AJ Franchuk
 */
//Checks that every player action changes the game data in the right way:
//the pet needs, the inventory and the apple tree.
//
//AI-assisted (Claude): the test cases were drafted with AI from the action
//classes and checked by hand against what each action actually does.
public class ActionHandlerTest {

    private ActionHandler handler;
    private Pet pet;
    private Player player;

    //Registers the same four actions as Main, and gives every test a pet
    //whose needs are all at 50 so a change in either direction shows up
    @Before
    public void setUp() {
        handler = new ActionHandler();
        handler.registerAction(new PutToBedAction());
        handler.registerAction(new FeedAction());
        handler.registerAction(new PlayGameAction());
        handler.registerAction(new CheckTreeAction());

        pet = new Pet("Whiskers", 1, new PetNeeds(50, 50, 50));
        player = new Player("AJ", 10, new AppleTree(3, 0));
    }

    // =====================================================================
    // Feed
    // =====================================================================

    @Test
    public void testFeed_IncreasesHungerByBuff() {
        FoodItem apple = new FoodItem("apple", 2, 10);
        player.getInventory().addItem(apple);
        handler.handleItemAction(apple, pet, player);
        assertEquals(60, pet.getNeeds().getHunger());
    }

    @Test
    public void testFeed_FoodIsRemovedFromInventory() {
        FoodItem apple = new FoodItem("apple", 2, 10);
        player.getInventory().addItem(apple);
        handler.handleItemAction(apple, pet, player);
        assertFalse(player.getInventory().hasItem(apple));
    }

    @Test
    public void testFeed_OnlyHungerChanges() {
        FoodItem apple = new FoodItem("apple", 2, 10);
        player.getInventory().addItem(apple);
        handler.handleItemAction(apple, pet, player);
        assertEquals(50, pet.getNeeds().getEnergy());
        assertEquals(50, pet.getNeeds().getHappiness());
    }

    @Test
    public void testFeed_HungerCappedAt100() {
        FoodItem feast = new FoodItem("feast", 2, 500);
        player.getInventory().addItem(feast);
        handler.handleItemAction(feast, pet, player);
        assertEquals(100, pet.getNeeds().getHunger());
    }

    @Test
    public void testFeed_Message() {
        FoodItem apple = new FoodItem("apple", 2, 10);
        player.getInventory().addItem(apple);
        String message = handler.handleItemAction(apple, pet, player);
        assertTrue(message.contains("Whiskers ate apple"));
    }

    @Test
    public void testFeed_WithoutItemChangesNothing() {
        String message = handler.handleAction("Feed", pet, player);
        assertEquals(50, pet.getNeeds().getHunger());
        assertEquals("", message);
    }

    // =====================================================================
    // Play Game
    // =====================================================================

    @Test
    public void testPlayGame_IncreasesHappinessByBuff() {
        ToyItem ball = new ToyItem("ball", 5, 20);
        player.getInventory().addItem(ball);
        handler.handleItemAction(ball, pet, player);
        assertEquals(70, pet.getNeeds().getHappiness());
    }

    @Test
    public void testPlayGame_ToyStaysInInventory() {
        ToyItem ball = new ToyItem("ball", 5, 20);
        player.getInventory().addItem(ball);
        handler.handleItemAction(ball, pet, player);
        assertTrue(player.getInventory().hasItem(ball));
    }

    @Test
    public void testPlayGame_HappinessCappedAt100() {
        ToyItem ball = new ToyItem("ball", 5, 20);
        player.getInventory().addItem(ball);
        for (int i = 0; i < 5; i++) {
            handler.handleItemAction(ball, pet, player);
        }
        assertEquals(100, pet.getNeeds().getHappiness());
    }

    @Test
    public void testPlayGame_NotAToy() {
        PlayGameAction play = new PlayGameAction();
        play.execute(pet, player, new FoodItem("apple", 2, 10));
        assertTrue(play.getMessage().contains("not a toy"));
        assertEquals(50, pet.getNeeds().getHappiness());
    }

    // =====================================================================
    // Put to Bed
    // =====================================================================

    @Test
    public void testPutToBed_RestoresFullEnergy() {
        handler.handleAction("Put to Bed", pet, player);
        assertEquals(100, pet.getNeeds().getEnergy());
    }

    @Test
    public void testPutToBed_OnlyEnergyChanges() {
        handler.handleAction("Put to Bed", pet, player);
        assertEquals(50, pet.getNeeds().getHunger());
        assertEquals(50, pet.getNeeds().getHappiness());
    }

    @Test
    public void testPutToBed_Message() {
        assertEquals("Whiskers is sleeping!", handler.handleAction("Put to Bed", pet, player));
    }

    @Test(expected = NullPointerException.class)
    public void testPutToBed_PetIsNull() {
        handler.handleAction("Put to Bed", null, player);
    }

    // =====================================================================
    // Check Apple Tree
    // =====================================================================

    @Test
    public void testCheckTree_PicksEveryApple() {
        handler.handleAction("Check Apple Tree", pet, player);
        assertEquals(3, player.getInventory().getItems().size());
        assertEquals(0, player.getGarden().getApples());
    }

    @Test
    public void testCheckTree_PickedApplesAreFood() {
        handler.handleAction("Check Apple Tree", pet, player);
        assertTrue(player.getInventory().findByName("apple") instanceof FoodItem);
    }

    @Test
    public void testCheckTree_BareTreeAddsNothing() {
        Player unlucky = new Player("AJ", 0, new AppleTree(0, 0));
        String message = handler.handleAction("Check Apple Tree", pet, unlucky);
        assertEquals(0, unlucky.getInventory().getItems().size());
        assertTrue(message.contains("Nothing ripe yet"));
    }

    @Test
    public void testCheckTree_PetNotChanged() {
        handler.handleAction("Check Apple Tree", pet, player);
        assertEquals(50, pet.getNeeds().getHunger());
        assertEquals(50, pet.getNeeds().getEnergy());
        assertEquals(50, pet.getNeeds().getHappiness());
    }

    @Test(expected = NullPointerException.class)
    public void testCheckTree_PlayerIsNull() {
        handler.handleAction("Check Apple Tree", pet, null);
    }

    // =====================================================================
    // Unknown input
    // =====================================================================

    @Test
    public void testHandleAction_UnknownAction() {
        assertEquals("Unknown action: Dance", handler.handleAction("Dance", pet, player));
        assertEquals(50, pet.getNeeds().getHunger());
    }

    @Test
    public void testHandleItemAction_PlainItemIsNotUsed() {
        Item rock = new Item("rock", 0, 99);
        player.getInventory().addItem(rock);
        assertEquals("Unknown action: ", handler.handleItemAction(rock, pet, player));
        assertTrue(player.getInventory().hasItem(rock));
    }

    @Test
    public void testHandleItemAction_NoActionsRegistered() {
        FoodItem apple = new FoodItem("apple", 2, 10);
        player.getInventory().addItem(apple);
        new ActionHandler().handleItemAction(apple, pet, player);
        assertTrue(player.getInventory().hasItem(apple)); //not wasted
    }

    // =====================================================================
    // Action details the GUI relies on
    // =====================================================================

    @Test
    public void testRequiresItem() {
        assertTrue(new FeedAction().requiresItem());
        assertTrue(new PlayGameAction().requiresItem());
        assertFalse(new PutToBedAction().requiresItem());
        assertFalse(new CheckTreeAction().requiresItem());
    }

    @Test
    public void testGetItemType() {
        assertEquals(FoodItem.class, new FeedAction().getItemType());
        assertEquals(ToyItem.class, new PlayGameAction().getItemType());
        assertNull(new PutToBedAction().getItemType());
    }
}
