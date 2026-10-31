package industries.angelic.adventuraarcanacore.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Some mods still register recipes for items their config switched off: BiblioCraft turns Reading Glasses into a
 * Reading book with the glasses off. Those recipes hold stacks without an item, which can't be crafted and crash code
 * that reads them, such as NEI's. Drop them.
 */
public final class BrokenRecipes {

    private BrokenRecipes() {}

    @SuppressWarnings("unchecked")
    public static void drop() {
        List<String> removed = new ArrayList<>();
        for (Iterator<IRecipe> it = CraftingManager.getInstance()
            .getRecipeList()
            .iterator(); it.hasNext();) {
            IRecipe recipe = it.next();
            if (broken(recipe.getRecipeOutput()) || brokenInputs(recipe)) {
                it.remove();
                removed.add(output(recipe.getRecipeOutput()));
            }
        }
        if (!removed.isEmpty()) AdventuraArcanaCore.LOG
            .info("Dropped {} recipes with items that aren't registered, making: {}", removed.size(), removed);
    }

    private static String output(ItemStack stack) {
        if (stack == null) return "nothing";
        if (stack.getItem() == null) return "a missing item";
        return Item.itemRegistry.getNameForObject(stack.getItem()) + ":" + stack.getItemDamage();
    }

    private static boolean broken(Object stack) {
        return stack instanceof ItemStack item && item.getItem() == null;
    }

    private static boolean brokenInputs(IRecipe recipe) {
        if (recipe instanceof ShapedRecipes shaped) return anyBroken(shaped.recipeItems);
        if (recipe instanceof ShapelessRecipes shapeless) return anyBroken(shapeless.recipeItems.toArray());
        if (recipe instanceof ShapedOreRecipe ore) return anyBroken(ore.getInput());
        if (recipe instanceof ShapelessOreRecipe ore) return anyBroken(
            ore.getInput()
                .toArray());
        return false;
    }

    private static boolean anyBroken(Object[] inputs) {
        for (Object input : inputs) {
            if (broken(input)) return true;
            if (input instanceof List<?>options) for (Object option : options) if (broken(option)) return true;
        }
        return false;
    }
}
