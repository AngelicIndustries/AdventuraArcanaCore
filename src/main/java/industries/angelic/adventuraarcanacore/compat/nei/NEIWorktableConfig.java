/* Recipe overlay adapted from Forestry's CustomOverlayHandler, by bdew. LGPL-3.0. */
package industries.angelic.adventuraarcanacore.compat.nei;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import industries.angelic.adventuraarcanacore.Config;
import industries.angelic.adventuraarcanacore.Tags;
import industries.angelic.adventuraarcanacore.worktable.WorktableNetwork;
import industries.angelic.adventuraarcanacore.worktable.client.GuiWorktable;

public final class NEIWorktableConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        if (Config.enableWorktable) API.registerGuiOverlayHandler(GuiWorktable.class, new Overlay(), "crafting");
    }

    @Override
    public String getName() {
        return "Adventura Arcana Core worktable";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }

    private static final class Overlay implements IOverlayHandler {

        @Override
        public boolean requireShiftForOverlayRecipe() {
            return false;
        }

        @Override
        public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int index, boolean shift) {
            ItemStack[] pattern = new ItemStack[9];
            for (PositionedStack ingredient : recipe.getIngredientStacks(index)) {
                int dx = ingredient.relx - 25;
                int dy = ingredient.rely - 6;
                if (dx < 0 || dy < 0 || dx > 36 || dy > 36 || dx % 18 != 0 || dy % 18 != 0) return;
                pattern[dx / 18 + dy / 18 * 3] = ingredient.item;
            }
            PositionedStack result = recipe.getResultStack(index);
            WorktableNetwork.CHANNEL.sendToServer(
                new WorktableNetwork.Transfer(
                    gui.inventorySlots.windowId,
                    pattern,
                    result == null ? null : result.item));
        }
    }
}
