package industries.angelic.adventuraarcanacore.compartment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCompartment extends Container {

    public static final int SEARCH = 8;
    public static final int CLOSE_SEARCH = 9;
    public static final int COLOR = 16;
    public static final int ICON = 32;
    public final TileCompartment tile;
    private int currentTab;
    private boolean searching;

    public ContainerCompartment(InventoryPlayer player, TileCompartment tile) {
        this.tile = tile;
        for (int i = 0; i < tile.getSizeInventory(); i++) addSlotToContainer(new Slot(tile, i, -10000, -10000));
        int width = guiWidth(tile.getTier());
        int playerX = (width - 162) / 2;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++)
                addSlotToContainer(new Slot(player, column + row * 9 + 9, playerX + column * 18, 150 + row * 18));
        }
        for (int column = 0; column < 9; column++)
            addSlotToContainer(new Slot(player, column, playerX + column * 18, 208));
        showTab();
    }

    public static int guiWidth(CompartmentTier tier) {
        return Math.max(248, 64 + tier.pageSize / 5 * 18);
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public boolean isSearching() {
        return searching;
    }

    public void showTab() {
        int columns = tile.getTier().pageSize / 5;
        int left = (guiWidth(tile.getTier()) - columns * 18) / 2;
        for (int i = 0; i < tile.getSizeInventory(); i++) {
            Slot slot = getSlot(i);
            boolean visible = i / tile.getTier().pageSize == currentTab;
            int index = i % tile.getTier().pageSize;
            slot.xDisplayPosition = visible ? left + index % columns * 18 : -10000;
            slot.yDisplayPosition = visible ? 28 + index / columns * 18 : -10000;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int action) {
        if (!canInteractWith(player)) return false;
        if (tile.isValidTab(action)) {
            currentTab = action;
            searching = false;
            showTab();
        } else if (action == SEARCH || action == CLOSE_SEARCH) {
            searching = action == SEARCH;
            if (!searching) showTab();
        } else if (action >= COLOR && action < COLOR + 16) {
            tile.getTab(currentTab)
                .setColor(action - COLOR);
            tile.tabChanged();
        } else if (action == ICON) {
            // The icon is copied from the server's cursor stack, never supplied by the client.
            tile.getTab(currentTab)
                .setIcon(player.inventory.getItemStack());
            tile.tabChanged();
        } else return false;
        return true;
    }

    private boolean accessible(int slot) {
        return slot >= tile.getSizeInventory() || searching || slot / tile.getTier().pageSize == currentTab;
    }

    @Override
    public ItemStack slotClick(int slot, int button, int mode, EntityPlayer player) {
        if (slot >= inventorySlots.size() || (slot >= 0 && !accessible(slot))) return null;
        return super.slotClick(slot, button, mode, player);
    }

    @Override
    public boolean canDragIntoSlot(Slot slot) {
        return accessible(slot.slotNumber);
    }

    @Override
    public boolean func_94530_a(ItemStack stack, Slot slot) {
        return accessible(slot.slotNumber);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < 0 || index >= inventorySlots.size() || !accessible(index)) return null;
        Slot slot = getSlot(index);
        if (!slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        int size = tile.getSizeInventory();
        if (index < size) {
            if (!mergeItemStack(stack, size, size + 36, true)) return null;
        } else {
            int start = currentTab * tile.getTier().pageSize;
            if (!mergeItemStack(stack, start, start + tile.getTier().pageSize, false)) return null;
        }
        if (stack.stackSize == 0) slot.putStack(null);
        else slot.onSlotChanged();
        if (stack.stackSize == original.stackSize) return null;
        slot.onPickupFromSlot(player, stack);
        return original;
    }
}
