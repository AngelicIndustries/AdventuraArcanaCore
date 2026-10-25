/* Adapted from Forestry's RecipeUtil. Copyright (c) 2011-2014 SirSengir and contributors. LGPL-3.0. */
package industries.angelic.adventuraarcanacore.worktable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public final class WorktableRecipes {

    private WorktableRecipes() {}

    public static InventoryCrafting matrix(ItemStack[] stacks) {
        InventoryCrafting matrix = new InventoryCrafting(new Container() {

            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);
        for (int i = 0; i < 9; i++) matrix.setInventorySlotContents(i, copy(stacks[i]));
        return matrix;
    }

    public static ItemStack copy(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return null;
        ItemStack copy = stack.copy();
        copy.stackSize = 1;
        return copy;
    }

    public static List<ItemStack> outputs(InventoryCrafting matrix, World world) {
        List<ItemStack> outputs = new ArrayList<>();
        ItemStack repair = repair(matrix);
        if (repair != null) {
            outputs.add(repair);
            return outputs;
        }
        for (Object entry : CraftingManager.getInstance()
            .getRecipeList()) {
            IRecipe recipe = (IRecipe) entry;
            if (!recipe.matches(matrix, world)) continue;
            ItemStack output = recipe.getCraftingResult(matrix);
            if (output == null || output.stackSize <= 0) continue;
            boolean duplicate = false;
            for (ItemStack existing : outputs) if (ItemStack.areItemStacksEqual(existing, output)) duplicate = true;
            if (!duplicate) outputs.add(output.copy());
        }
        return outputs;
    }

    public static boolean matchesOutput(InventoryCrafting matrix, World world, ItemStack expected) {
        for (ItemStack output : outputs(matrix, world)) if (ItemStack.areItemStacksEqual(output, expected)) return true;
        return false;
    }

    private static ItemStack repair(InventoryCrafting matrix) {
        ItemStack first = null;
        ItemStack second = null;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = matrix.getStackInSlot(i);
            if (stack == null) continue;
            if (first == null) first = stack;
            else if (second == null) second = stack;
            else return null;
        }
        if (first == null || second == null
            || first.stackSize != 1
            || second.stackSize != 1
            || first.getItem() != second.getItem()
            || !first.getItem()
                .isRepairable())
            return null;
        int maximum = first.getMaxDamage();
        int damage = maximum - (maximum - first.getItemDamageForDisplay()
            + maximum
            - second.getItemDamageForDisplay()
            + maximum * 5 / 100);
        return new ItemStack(first.getItem(), 1, Math.max(0, damage));
    }

    public static boolean equivalent(ItemStack pattern, ItemStack available, boolean substitutes) {
        if (pattern == null || available == null || available.stackSize <= 0) return false;
        boolean tags = !pattern.hasTagCompound() || pattern.getTagCompound()
            .hasNoTags() || ItemStack.areItemStackTagsEqual(pattern, available);
        if (pattern.getItem() == available.getItem()) {
            if (tags && (pattern.getItemDamage() == OreDictionary.WILDCARD_VALUE
                || pattern.getItemDamage() == available.getItemDamage())) return true;
            if (substitutes && !pattern.getItem()
                .doesContainerItemLeaveCraftingGrid(pattern)) {
                if (!pattern.hasTagCompound() || pattern.getTagCompound()
                    .hasNoTags()
                    || pattern.getItemDamage() == OreDictionary.WILDCARD_VALUE
                    || pattern.getItemDamage() == available.getItemDamage()) return true;
            }
        }
        if (!substitutes || !tags) return false;
        for (int a : OreDictionary.getOreIDs(pattern))
            for (int b : OreDictionary.getOreIDs(available)) if (a == b) return true;
        return false;
    }

    public static NBTTagList writeStacks(ItemStack[] stacks) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] == null) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("Slot", i);
            stacks[i].writeToNBT(tag);
            list.appendTag(tag);
        }
        return list;
    }

    public static ItemStack[] readStacks(NBTTagList list, int size, boolean ghost) {
        ItemStack[] stacks = new ItemStack[size];
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("Slot");
            if (slot < 0 || slot >= size) continue;
            ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
            if (stack == null || stack.stackSize <= 0) continue;
            stack.stackSize = ghost ? 1 : Math.min(stack.stackSize, Math.min(64, stack.getMaxStackSize()));
            stacks[slot] = stack;
        }
        return stacks;
    }
}
