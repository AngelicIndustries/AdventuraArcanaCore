/* Worktable behavior adapted from Forestry. Copyright (c) 2011-2014 SirSengir and contributors. LGPL-3.0. */
package industries.angelic.adventuraarcanacore.worktable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class TileWorktable extends TileEntity implements IInventory {

    public static final int STORAGE_SIZE = 18;
    private ItemStack[] items = new ItemStack[STORAGE_SIZE];
    private ItemStack[] pattern = new ItemStack[9];
    private final RecipeMemory memory = new RecipeMemory();
    private final List<IRecipe> candidates = new ArrayList<>();
    private List<ItemStack> outputs = new ArrayList<>();
    private ItemStack selectedOutput;
    private boolean recipeDirty = true;
    private int source;

    public RecipeMemory getMemory() {
        return memory;
    }

    public ItemStack getPattern(int slot) {
        return WorktableRecipes.copy(pattern[slot]);
    }

    public int getSource() {
        return source;
    }

    public void cycleSource() {
        source = (source + 1) % 3;
        markDirty();
    }

    public void setPattern(int slot, ItemStack stack) {
        if (slot < 0 || slot >= 9) return;
        pattern[slot] = WorktableRecipes.copy(stack);
        recipeDirty = true;
        markDirty();
    }

    public void setPattern(ItemStack[] stacks) {
        for (int i = 0; i < 9; i++) pattern[i] = WorktableRecipes.copy(stacks[i]);
        recipeDirty = true;
        markDirty();
    }

    public void clearPattern() {
        setPattern(new ItemStack[9]);
    }

    private void refreshRecipe() {
        if (!recipeDirty) return;
        recipeDirty = false;
        InventoryCrafting matrix = WorktableRecipes.matrix(pattern);
        outputs = WorktableRecipes.outputs(matrix, worldObj);
        candidates.clear();
        for (Object entry : CraftingManager.getInstance()
            .getRecipeList()) {
            IRecipe recipe = (IRecipe) entry;
            if (recipe.matches(matrix, worldObj)) candidates.add(recipe);
        }
        for (ItemStack output : outputs) if (ItemStack.areItemStacksEqual(output, selectedOutput)) return;
        selectedOutput = outputs.isEmpty() ? null
            : outputs.get(0)
                .copy();
    }

    public ItemStack getOutput() {
        refreshRecipe();
        return selectedOutput == null ? null : selectedOutput.copy();
    }

    public boolean hasConflicts() {
        refreshRecipe();
        return outputs.size() > 1;
    }

    public void cycleRecipe(int direction) {
        refreshRecipe();
        if (outputs.isEmpty()) return;
        int selected = 0;
        for (int i = 0; i < outputs.size(); i++)
            if (ItemStack.areItemStacksEqual(outputs.get(i), selectedOutput)) selected = i;
        selectedOutput = outputs.get((selected + direction + outputs.size()) % outputs.size())
            .copy();
        markDirty();
    }

    public void preferOutput(ItemStack preferred) {
        refreshRecipe();
        for (ItemStack output : outputs) if (ItemStack.areItemStacksEqual(output, preferred)) {
            selectedOutput = output.copy();
            markDirty();
            return;
        }
    }

    public void selectMemory(int index) {
        RecipeMemory.Entry entry = memory.get(index);
        if (entry == null) return;
        setPattern(entry.pattern);
        selectedOutput = entry.output.copy();
        refreshRecipe();
    }

    public List<SourceSlot> sources(EntityPlayer player) {
        List<SourceSlot> slots = new ArrayList<>();
        if (source != 1) for (int i = 0; i < STORAGE_SIZE; i++) slots.add(new SourceSlot(this, i));
        if (source != 0) for (int i = 0; i < 36; i++) slots.add(new SourceSlot(player.inventory, i));
        return slots;
    }

    public CraftPlan plan(EntityPlayer player) {
        ItemStack output = getOutput();
        if (output == null) return null;
        List<SourceSlot> sources = sources(player);
        int[] remaining = new int[sources.size()];
        for (int i = 0; i < remaining.length; i++) {
            ItemStack stack = sources.get(i)
                .get();
            remaining[i] = stack == null ? 0 : stack.stackSize;
        }
        ItemStack[] ingredients = new ItemStack[9];
        int[] chosen = new int[9];
        Arrays.fill(chosen, -1);
        for (int slot = 0; slot < 9; slot++) {
            if (pattern[slot] == null) continue;
            for (int pass = 0; pass < 2 && chosen[slot] == -1; pass++) {
                for (int i = 0; i < sources.size(); i++) {
                    if (remaining[i] == 0 || !WorktableRecipes.equivalent(
                        pattern[slot],
                        sources.get(i)
                            .get(),
                        pass == 1)) continue;
                    ingredients[slot] = WorktableRecipes.copy(
                        sources.get(i)
                            .get());
                    chosen[slot] = i;
                    remaining[i]--;
                    break;
                }
            }
            if (chosen[slot] == -1) return null;
        }
        InventoryCrafting matrix = WorktableRecipes.matrix(ingredients);
        boolean valid = false;
        for (IRecipe recipe : candidates) {
            if (recipe.matches(matrix, worldObj)
                && ItemStack.areItemStacksEqual(output, recipe.getCraftingResult(matrix))) {
                valid = true;
                break;
            }
        }
        if (!valid && !WorktableRecipes.matchesOutput(matrix, worldObj, output)) return null;
        return new CraftPlan(matrix, output, sources, chosen);
    }

    public void consume(CraftPlan plan) {
        for (int chosen : plan.chosen) if (chosen >= 0) {
            SourceSlot source = plan.sources.get(chosen);
            source.inventory.decrStackSize(source.slot, 1);
        }
    }

    public void complete(EntityPlayer player, CraftPlan plan) {
        ItemStack[] used = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            ItemStack ingredient = plan.matrix.getStackInSlot(i);
            used[i] = WorktableRecipes.copy(ingredient);
            if (ingredient == null) continue;
            ItemStack remainder = ingredient.getItem()
                .hasContainerItem(ingredient)
                    ? ingredient.getItem()
                        .getContainerItem(ingredient)
                    : null;
            if (remainder == null && ingredient.stackSize > 1) {
                remainder = ingredient.copy();
                remainder.stackSize--;
            }
            if (remainder == null) continue;
            if (remainder.isItemStackDamageable() && remainder.getItemDamage() > remainder.getMaxDamage()) {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, remainder));
                continue;
            }
            insert(plan.sources, remainder);
            if (remainder.stackSize > 0) player.dropPlayerItemWithRandomChoice(remainder, false);
        }
        setPattern(used);
        memory.remember(used, plan.output);
        markDirty();
    }

    public static void insert(List<SourceSlot> slots, ItemStack stack) {
        for (int pass = 0; pass < 2; pass++) for (SourceSlot slot : slots) {
            if (stack.stackSize <= 0) return;
            ItemStack existing = slot.get();
            if (!slot.inventory.isItemValidForSlot(slot.slot, stack)) continue;
            if (pass == 0 && (existing == null || !sameItem(existing, stack))) continue;
            if (pass == 1 && existing != null) continue;
            int maximum = Math.min(slot.inventory.getInventoryStackLimit(), stack.getMaxStackSize());
            int amount = Math.min(stack.stackSize, maximum - (existing == null ? 0 : existing.stackSize));
            if (amount <= 0) continue;
            if (existing == null) slot.inventory.setInventorySlotContents(slot.slot, stack.splitStack(amount));
            else {
                existing.stackSize += amount;
                stack.stackSize -= amount;
                slot.inventory.markDirty();
            }
        }
    }

    public static boolean sameItem(ItemStack a, ItemStack b) {
        return a != null && b != null && a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    public static final class CraftPlan {

        public final InventoryCrafting matrix;
        public final ItemStack output;
        private final List<SourceSlot> sources;
        private final int[] chosen;

        private CraftPlan(InventoryCrafting matrix, ItemStack output, List<SourceSlot> sources, int[] chosen) {
            this.matrix = matrix;
            this.output = output;
            this.sources = sources;
            this.chosen = chosen;
        }
    }

    public static final class SourceSlot {

        public final IInventory inventory;
        public final int slot;

        public SourceSlot(IInventory inventory, int slot) {
            this.inventory = inventory;
            this.slot = slot;
        }

        public ItemStack get() {
            return inventory.getStackInSlot(slot);
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return STORAGE_SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < STORAGE_SIZE ? items[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null || amount <= 0) return null;
        ItemStack removed = stack.splitStack(Math.min(amount, stack.stackSize));
        if (stack.stackSize <= 0) items[slot] = null;
        markDirty();
        return removed;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return decrStackSize(slot, Integer.MAX_VALUE);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= STORAGE_SIZE) return;
        items[slot] = stack;
        if (stack != null) stack.stackSize = Math.min(stack.stackSize, Math.min(64, stack.getMaxStackSize()));
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "tile.adventuraarcanacore.worktable.name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj != null && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
            && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot >= 0 && slot < STORAGE_SIZE;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("Items", WorktableRecipes.writeStacks(items));
        nbt.setTag("Pattern", WorktableRecipes.writeStacks(pattern));
        nbt.setTag("RecipeMemory", memory.write());
        nbt.setInteger("Source", source);
        if (selectedOutput != null) nbt.setTag("SelectedOutput", selectedOutput.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        items = WorktableRecipes.readStacks(nbt.getTagList("Items", 10), STORAGE_SIZE, false);
        pattern = WorktableRecipes.readStacks(nbt.getTagList("Pattern", 10), 9, true);
        memory.read(nbt.getTagList("RecipeMemory", 10));
        source = Math.max(0, Math.min(2, nbt.getInteger("Source")));
        selectedOutput = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("SelectedOutput"));
        recipeDirty = true;
    }
}
