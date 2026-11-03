package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;

public class KitchenHandsTest {

    @Test
    public void ovenFacingSouthUsesTheHitAsIs() {
        // Cooking for Blockheads' own quadrant order for metadata 3.
        assertEquals(17, KitchenHands.ovenToolSlot(3, 0.25F, 0.25F));
        assertEquals(16, KitchenHands.ovenToolSlot(3, 0.75F, 0.25F));
        assertEquals(18, KitchenHands.ovenToolSlot(3, 0.25F, 0.75F));
        assertEquals(19, KitchenHands.ovenToolSlot(3, 0.75F, 0.75F));
    }

    @Test
    public void otherFacingsRotateTheHitLikeTheOven() {
        assertEquals(19, KitchenHands.ovenToolSlot(2, 0.25F, 0.25F));
        assertEquals(17, KitchenHands.ovenToolSlot(2, 0.75F, 0.75F));
        assertEquals(19, KitchenHands.ovenToolSlot(4, 0.25F, 0.75F));
        assertEquals(16, KitchenHands.ovenToolSlot(4, 0.75F, 0.75F));
        assertEquals(17, KitchenHands.ovenToolSlot(5, 0.25F, 0.75F));
        assertEquals(16, KitchenHands.ovenToolSlot(5, 0.25F, 0.25F));
    }

    @Test
    public void everyFacingReachesAllFourCookwareSlots() {
        float[] at = { 0.25F, 0.75F };
        for (int metadata = 2; metadata <= 5; metadata++) {
            Set<Integer> slots = new HashSet<>();
            for (float x : at) for (float z : at) slots.add(KitchenHands.ovenToolSlot(metadata, x, z));
            assertEquals("metadata " + metadata, 4, slots.size());
            for (int slot : slots) assertTrue(slot >= 16 && slot < 20);
        }
    }

    @Test
    public void recipeListCoversTheGridAndScrollbarOnly() {
        assertTrue(KitchenHands.overRecipeList(102, 11));
        assertTrue(KitchenHands.overRecipeList(160, 80));
        assertTrue(KitchenHands.overRecipeList(97, 7));
        assertTrue(KitchenHands.overRecipeList(167, 84));
        // Crafting grid, player inventory, the screen's title bar and NEI beside the screen.
        assertFalse(KitchenHands.overRecipeList(40, 40));
        assertFalse(KitchenHands.overRecipeList(120, 100));
        assertFalse(KitchenHands.overRecipeList(120, 6));
        assertFalse(KitchenHands.overRecipeList(-20, 40));
        assertFalse(KitchenHands.overRecipeList(168, 40));
    }

    private final Item bread = new Item();

    @Test
    public void takenBreadTopsUpCarriedBreadBeforeTheHand() {
        InventoryPlayer inventory = new InventoryPlayer(null);
        inventory.currentItem = 4;
        inventory.setInventorySlotContents(1, new ItemStack(bread, 10));
        assertNull(KitchenHands.store(inventory, new ItemStack(bread, 2)));
        assertEquals(12, inventory.getStackInSlot(1).stackSize);
        assertNull(inventory.getCurrentItem());
    }

    @Test
    public void takenBreadGoesToTheEmptyHandWhenNothingMatches() {
        InventoryPlayer inventory = new InventoryPlayer(null);
        inventory.currentItem = 4;
        inventory.setInventorySlotContents(0, new ItemStack(new Item(), 5));
        assertNull(KitchenHands.store(inventory, new ItemStack(bread, 1)));
        assertEquals(1, inventory.getCurrentItem().stackSize);
        // A second piece now tops up the hand instead of starting another stack.
        assertNull(KitchenHands.store(inventory, new ItemStack(bread, 1)));
        assertEquals(2, inventory.getCurrentItem().stackSize);
    }

    @Test
    public void aFullStackOverflowsIntoTheHand() {
        InventoryPlayer inventory = new InventoryPlayer(null);
        inventory.currentItem = 3;
        inventory.setInventorySlotContents(0, new ItemStack(bread, 63));
        assertNull(KitchenHands.store(inventory, new ItemStack(bread, 2)));
        assertEquals(64, inventory.getStackInSlot(0).stackSize);
        assertEquals(1, inventory.getCurrentItem().stackSize);
    }
}
