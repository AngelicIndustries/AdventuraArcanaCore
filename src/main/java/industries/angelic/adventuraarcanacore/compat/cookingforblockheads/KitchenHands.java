package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** Empty-hand kitchen interactions and the recipe list's area, shared by the Cooking for Blockheads patches. */
public final class KitchenHands {

    /** The oven's four cookware slots follow its 16 GUI slots. */
    private static final int OVEN_TOOL_SLOTS = 16;

    private KitchenHands() {}

    /** The cookware slot under a hit on the oven's top, as Cooking for Blockheads places cookware. */
    public static int ovenToolSlot(int metadata, float subX, float subZ) {
        float hitX = subX, hitZ = subZ;
        switch (metadata) {
            case 2:
                hitX = 1 - subX;
                hitZ = 1 - subZ;
                break;
            case 4:
                hitX = subZ;
                hitZ = 1 - subX;
                break;
            case 5:
                hitX = 1 - subZ;
                hitZ = subX;
                break;
            default:
                break;
        }
        int quadrant = hitZ < 0.5F ? (hitX < 0.5F ? 1 : 0) : (hitX < 0.5F ? 2 : 3);
        return OVEN_TOOL_SLOTS + quadrant;
    }

    /** Moves every stack of an inventory to the player, as picking it up would. */
    public static boolean takeAll(IInventory from, EntityPlayer player) {
        boolean took = false;
        for (int i = 0; i < from.getSizeInventory(); i++) {
            ItemStack stack = from.getStackInSlot(i);
            if (stack == null) continue;
            from.setInventorySlotContents(i, null);
            ItemStack left = store(player.inventory, stack);
            // Both sides run this; only the server drops, so the client shows no ghost item.
            if (left != null && !player.worldObj.isRemote) player.dropPlayerItemWithRandomChoice(left, false);
            took = true;
        }
        return took;
    }

    /**
     * Tops up the stacks the player carries before starting a new one, in their empty hand if they have one. A second
     * stack of the same item made NoHotbarNeeded show the item's total on selecting either. Returns what didn't fit.
     */
    static ItemStack store(InventoryPlayer inventory, ItemStack stack) {
        int limit = inventory.getInventoryStackLimit();
        for (ItemStack carried : inventory.mainInventory) {
            if (carried == null || !carried.isItemEqual(stack) || !ItemStack.areItemStackTagsEqual(carried, stack))
                continue;
            int moved = Math.min(stack.stackSize, Math.min(carried.getMaxStackSize(), limit) - carried.stackSize);
            if (moved <= 0) continue;
            carried.stackSize += moved;
            stack.stackSize -= moved;
            if (stack.stackSize == 0) return null;
        }
        if (inventory.getCurrentItem() == null) {
            inventory.setInventorySlotContents(inventory.currentItem, stack);
            return null;
        }
        inventory.addItemStackToInventory(stack);
        return stack.stackSize > 0 ? stack : null;
    }

    /** The recipe grid and its scrollbar, relative to the recipe book screen's corner. */
    public static boolean overRecipeList(int x, int y) {
        return x >= 97 && x < 168 && y >= 7 && y < 85;
    }
}
