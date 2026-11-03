package industries.angelic.adventuraarcanacore.compat.nei;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.Toasts;

public final class ToasterRecipeHandler extends TemplateRecipeHandler {

    public static final String ID = "adventuraarcanacore.toaster";

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("adventuraarcanacore.nei.toaster");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/furnace.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return ID;
    }

    @Override
    public String specifyTransferRect() {
        return ID;
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(72, 10, 24, 17), ID));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (ID.equals(outputId)) {
            for (Toasts.Recipe recipe : Toasts.recipes()) arecipes.add(new ToastRecipe(recipe));
        } else super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null) return;
        for (Toasts.Recipe recipe : Toasts.recipes()) if (recipe.output()
            .isItemEqual(result)) arecipes.add(new ToastRecipe(recipe));
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        ItemStack toaster = GameRegistry.findItemStack(Toasts.MOD, "toaster", 1);
        for (Toasts.Recipe recipe : Toasts.recipes())
            if (recipe.matches(ingredient) || toaster != null && toaster.isItemEqual(ingredient))
                arecipes.add(new ToastRecipe(recipe));
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        // Reuse vanilla slots and arrow; the toaster has no GUI of its own.
        GuiDraw.drawTexturedModalRect(34, 9, 55, 16, 18, 18);
        GuiDraw.drawTexturedModalRect(112, 5, 111, 30, 26, 26);
        GuiDraw.drawTexturedModalRect(72, 10, 79, 34, 24, 17);
    }

    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(72, 10, 176, 14, 24, 16, 60, 0);
        GuiDraw.drawStringC(
            StatCollector.translateToLocal("adventuraarcanacore.nei.toaster.time"),
            83,
            50,
            0x404040,
            false);
        GuiDraw.drawStringC(
            StatCollector.translateToLocal("adventuraarcanacore.nei.toaster.insert"),
            83,
            62,
            0x404040,
            false);
        GuiDraw.drawStringC(
            StatCollector.translateToLocal("adventuraarcanacore.nei.toaster.start"),
            83,
            72,
            0x404040,
            false);
    }

    public final class ToastRecipe extends CachedRecipe {

        private final PositionedStack input, output;

        ToastRecipe(Toasts.Recipe recipe) {
            input = new PositionedStack(recipe.input(), 35, 10);
            output = new PositionedStack(recipe.output(), 117, 10);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return Collections.singletonList(input);
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }

        @Override
        public PositionedStack getOtherStack() {
            ItemStack toaster = GameRegistry.findItemStack(Toasts.MOD, "toaster", 1);
            return toaster == null ? null : new PositionedStack(toaster, 76, 30);
        }
    }
}
