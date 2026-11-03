package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.core.PatchSwitches;

/** Bread recipes shared by the toaster and NEI. Other mods' toaster recipes are left alone. */
public final class Toasts {

    public static final String MOD = "cookingforblockheads";
    private static final List<Recipe> RECIPES = new ArrayList<>();
    private static Item pamToast;
    private static Item oldToast;

    public enum Kind {

        CORN("corn_bread_toast", "plantmegapack", "foodCornBread", "foodCornBread"),
        SALTED("salted_toast", "saltymod", "salt_bread", "salt_bread"),
        ABYSSAL("abyssal_toast", "netherlicious", "AbyssalBread", "abyssal_bread");

        public final String id, mod, bread, texture;
        private ItemToast item;

        Kind(String id, String mod, String bread, String texture) {
            this.id = id;
            this.mod = mod;
            this.bread = bread;
            this.texture = texture;
        }
    }

    private Toasts() {}

    public static boolean enabled() {
        return PatchSwitches.enabled("toasterBread") && Loader.isModLoaded(MOD) && Loader.isModLoaded("harvestcraft");
    }

    public static void register() {
        if (!enabled()) return;
        for (Kind kind : Kind.values()) {
            if (!Loader.isModLoaded(kind.mod)) continue;
            kind.item = new ItemToast(kind);
            GameRegistry.registerItem(kind.item, kind.id);
            OreDictionary.registerOre("foodToast", kind.item);
        }
    }

    public static void configure() {
        if (!enabled()) return;
        pamToast = GameRegistry.findItem("harvestcraft", "toastItem");
        if (pamToast == null) return;
        oldToast = GameRegistry.findItem(MOD, "toast");
        RECIPES.clear();
        RECIPES.add(new Recipe(new ItemStack(Items.bread), new ItemStack(pamToast)));
        for (Kind kind : Kind.values()) {
            Item bread = GameRegistry.findItem(kind.mod, kind.bread);
            if (kind.item == null) continue;
            if (bread != null) RECIPES.add(new Recipe(new ItemStack(bread), new ItemStack(kind.item)));
            else kind.item.setCreativeTab(null);
        }
        if (oldToast != null) oldToast.setCreativeTab(null);
        removeOtherRecipes();
    }

    public static Item pamToast() {
        return pamToast;
    }

    public static Item oldToast() {
        return oldToast;
    }

    public static List<Recipe> recipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    /** A fresh single toast for each occupied toaster slot; the input stack is never changed here. */
    public static ItemStack output(ItemStack input) {
        for (Recipe recipe : RECIPES) if (recipe.matches(input)) return recipe.output();
        return null;
    }

    public static ItemStack replaceOldToast(ItemStack stack) {
        if (stack == null || oldToast == null || stack.getItem() != oldToast || pamToast == null) return stack;
        ItemStack replacement = new ItemStack(pamToast, stack.stackSize);
        if (stack.hasTagCompound()) replacement.setTagCompound(
            (net.minecraft.nbt.NBTTagCompound) stack.getTagCompound()
                .copy());
        return replacement;
    }

    /** Run again at loadComplete, after other mods have finished adding recipes. */
    public static void removeOtherRecipes() {
        if (pamToast == null) return;
        Map<ItemStack, ItemStack> smelting = FurnaceRecipes.smelting()
            .getSmeltingList();
        List<IRecipe> crafting = CraftingManager.getInstance()
            .getRecipeList();
        smelting.entrySet()
            .removeIf(entry -> isToast(entry.getValue()));
        crafting.removeIf(recipe -> recipe != null && isToast(recipe.getRecipeOutput()));
    }

    private static boolean isToast(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        if (item == pamToast || item == oldToast) return true;
        for (Kind kind : Kind.values()) if (item == kind.item) return true;
        return false;
    }

    public static final class Recipe {

        private final ItemStack input, output;

        public Recipe(ItemStack input, ItemStack output) {
            this.input = input.copy();
            this.output = output.copy();
            this.input.stackSize = 1;
            this.output.stackSize = 1;
        }

        public boolean matches(ItemStack stack) {
            return stack != null && stack.stackSize > 0 && input.isItemEqual(stack);
        }

        public ItemStack input() {
            return input.copy();
        }

        public ItemStack output() {
            return output.copy();
        }
    }
}
