package industries.angelic.adventuraarcanacore.worktable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.FMLCommonHandler;

public class ContainerWorktable extends Container {

    public static final int CLEAR = 18;
    public static final int PREVIOUS = 19;
    public static final int NEXT = 20;
    public static final int SOURCE = 21;
    public static final int STORAGE_START = 19;
    public static final int PLAYER_START = 37;
    private final EntityPlayer player;
    public final TileWorktable tile;
    private final InventoryBasic display = new InventoryBasic("worktable", false, 19);
    private final ResultSlot resultSlot;
    private final int[] state = { -1, -1, -1, -1 };
    public int locks;
    public int source;
    public boolean conflicts;
    public boolean craftable;

    public ContainerWorktable(EntityPlayer player, TileWorktable tile) {
        this.player = player;
        this.tile = tile;
        resultSlot = new ResultSlot(player, display);
        addSlotToContainer(resultSlot);
        for (int i = 0; i < 9; i++)
            addSlotToContainer(new DisplaySlot(display, i + 1, 11 + i % 3 * 18, 20 + i / 3 * 18));
        for (int i = 0; i < 9; i++)
            addSlotToContainer(new DisplaySlot(display, i + 10, 110 + i % 3 * 18, 20 + i / 3 * 18));
        for (int i = 0; i < 18; i++) addSlotToContainer(new Slot(tile, i, 8 + i % 9 * 18, 90 + i / 9 * 18));
        for (int i = 0; i < 27; i++)
            addSlotToContainer(new Slot(player.inventory, i + 9, 8 + i % 9 * 18, 136 + i / 9 * 18));
        for (int i = 0; i < 9; i++) addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 194));
        if (tile.getWorldObj() == null || !tile.getWorldObj().isRemote) refreshDisplay();
    }

    private void refreshDisplay() {
        display.setInventorySlotContents(0, tile.getOutput());
        for (int i = 0; i < 9; i++) {
            display.setInventorySlotContents(i + 1, tile.getPattern(i));
            RecipeMemory.Entry memory = tile.getMemory()
                .get(i);
            display.setInventorySlotContents(i + 10, memory == null ? null : memory.output.copy());
        }
        locks = tile.getMemory()
            .lockMask();
        source = tile.getSource();
        conflicts = tile.hasConflicts();
        craftable = tile.plan(player) != null;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);
        for (int i = 0; i < state.length; i++) crafter.sendProgressBarUpdate(this, i, value(i));
    }

    private int value(int id) {
        return id == 0 ? locks : id == 1 ? source : id == 2 ? (conflicts ? 1 : 0) : (craftable ? 1 : 0);
    }

    @Override
    public void detectAndSendChanges() {
        if (player.worldObj != null && player.worldObj.isRemote) return;
        refreshDisplay();
        super.detectAndSendChanges();
        for (int i = 0; i < state.length; i++) {
            int value = value(i);
            if (state[i] == value) continue;
            state[i] = value;
            for (Object entry : crafters) ((ICrafting) entry).sendProgressBarUpdate(this, i, value);
        }
    }

    @Override
    public void updateProgressBar(int id, int value) {
        if (id == 0) locks = value;
        else if (id == 1) source = value;
        else if (id == 2) conflicts = value != 0;
        else if (id == 3) craftable = value != 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int action) {
        if (!canInteractWith(player) || (player.worldObj != null && player.worldObj.isRemote)) return false;
        if (action >= 0 && action < 9) tile.selectMemory(action);
        else if (action >= 9 && action < 18) {
            tile.getMemory()
                .toggleLock(action - 9);
            tile.markDirty();
        } else if (action == CLEAR) tile.clearPattern();
        else if (action == PREVIOUS || action == NEXT) tile.cycleRecipe(action == PREVIOUS ? -1 : 1);
        else if (action == SOURCE) tile.cycleSource();
        else return false;
        detectAndSendChanges();
        return true;
    }

    @Override
    public ItemStack slotClick(int slot, int button, int mode, EntityPlayer player) {
        if (!canInteractWith(player)) return null;
        if (slot >= 0 && slot < STORAGE_START) {
            // Display slots never participate in vanilla item movement, drag splitting or double-click collection.
            if (player.worldObj != null && player.worldObj.isRemote) return null;
            if (slot == 0) craft(player, button, mode);
            else if (slot < 10 && (mode == 0 || mode == 1) && (button == 0 || button == 1))
                tile.setPattern(slot - 1, button == 1 ? null : player.inventory.getItemStack());
            else if (slot >= 10 && mode == 0 && (button == 0 || button == 1))
                enchantItem(player, slot - 10 + (button == 1 ? 9 : 0));
            detectAndSendChanges();
            if (player instanceof EntityPlayerMP serverPlayer) serverPlayer.updateHeldItem();
            return null;
        }
        return super.slotClick(slot, button, mode, player);
    }

    private void craft(EntityPlayer player, int button, int mode) {
        if (mode != 0 && mode != 1 && mode != 2 && mode != 4) return;
        if (mode == 2 ? button < 0 || button > 8 : button < 0 || button > 1) return;
        long deadline = System.nanoTime() + 20_000_000L;
        int crafted = 0;
        ItemStack firstOutput = null;
        do {
            TileWorktable.CraftPlan plan = tile.plan(player);
            if (plan == null) return;
            ItemStack output = plan.output.copy();
            if (firstOutput == null) firstOutput = output.copy();
            if (!TileWorktable.sameItem(firstOutput, output)
                || crafted + output.stackSize > Math.max(output.stackSize, output.getMaxStackSize())) return;
            ItemStack target = mode == 2 ? player.inventory.getStackInSlot(button) : player.inventory.getItemStack();
            if ((mode == 0 || mode == 2) && !fits(target, output)) return;
            List<TileWorktable.SourceSlot> playerSlots = new ArrayList<>();
            if (mode == 1) {
                for (int i = 0; i < 36; i++) playerSlots.add(new TileWorktable.SourceSlot(player.inventory, i));
                if (!fitsInventory(playerSlots, output)) return;
            }
            tile.consume(plan);
            int amount = output.stackSize;
            if (mode == 4) player.dropPlayerItemWithRandomChoice(output, false);
            else if (mode == 1) TileWorktable.insert(playerSlots, output);
            else {
                if (target == null) target = output;
                else target.stackSize += amount;
                if (mode == 2) player.inventory.setInventorySlotContents(button, target);
                else player.inventory.setItemStack(target);
            }
            resultSlot.crafted(player, plan.output.copy(), plan.matrix);
            tile.complete(player, plan);
            player.inventory.markDirty();
            crafted += amount;
        } while (mode == 1 && crafted < firstOutput.getMaxStackSize() && System.nanoTime() < deadline);
    }

    private static boolean fits(ItemStack target, ItemStack output) {
        return target == null ? output.stackSize <= output.getMaxStackSize()
            : TileWorktable.sameItem(target, output) && target.stackSize + output.stackSize <= target.getMaxStackSize();
    }

    private static boolean fitsInventory(List<TileWorktable.SourceSlot> slots, ItemStack output) {
        int room = 0;
        for (TileWorktable.SourceSlot slot : slots) {
            ItemStack stack = slot.get();
            if (stack == null) room += Math.min(64, output.getMaxStackSize());
            else if (TileWorktable.sameItem(stack, output))
                room += Math.max(0, Math.min(64, stack.getMaxStackSize()) - stack.stackSize);
            if (room >= output.stackSize) return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < STORAGE_START || index >= inventorySlots.size()) return null;
        Slot slot = getSlot(index);
        if (!slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (index < PLAYER_START) {
            if (!mergeItemStack(stack, PLAYER_START, inventorySlots.size(), true)) return null;
        } else if (!mergeItemStack(stack, STORAGE_START, PLAYER_START, false)) return null;
        if (stack.stackSize == 0) slot.putStack(null);
        else slot.onSlotChanged();
        return original;
    }

    @Override
    public boolean canDragIntoSlot(Slot slot) {
        return slot.slotNumber >= STORAGE_START;
    }

    @Override
    public boolean func_94530_a(ItemStack stack, Slot slot) {
        return slot.slotNumber >= STORAGE_START;
    }

    private static class DisplaySlot extends Slot {

        DisplaySlot(IInventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer player) {
            return false;
        }
    }

    private static final class ResultSlot extends DisplaySlot {

        private final CraftingEvents events;

        ResultSlot(EntityPlayer player, IInventory display) {
            super(display, 0, 77, 38);
            events = new CraftingEvents(player, display);
        }

        void crafted(EntityPlayer player, ItemStack output, InventoryCrafting matrix) {
            FMLCommonHandler.instance()
                .firePlayerCraftingEvent(player, output, matrix);
            events.crafted(output);
        }
    }

    // Keep the visible result a normal slot: EntityPlayerMP omits SlotCrafting from incremental slot updates.
    private static final class CraftingEvents extends SlotCrafting {

        CraftingEvents(EntityPlayer player, IInventory display) {
            super(player, display, display, 0, 0, 0);
        }

        void crafted(ItemStack output) {
            onCrafting(output, output.stackSize);
        }
    }
}
