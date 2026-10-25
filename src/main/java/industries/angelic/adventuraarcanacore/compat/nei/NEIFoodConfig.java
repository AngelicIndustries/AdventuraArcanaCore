package industries.angelic.adventuraarcanacore.compat.nei;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import industries.angelic.adventuraarcanacore.Tags;
import industries.angelic.adventuraarcanacore.food.GrowthcraftGrapes;
import industries.angelic.adventuraarcanacore.food.PamFood;

/** Hides food items another mod makes redundant: HarvestCraft's grape when Growthcraft's is the only grape. */
public final class NEIFoodConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        if (!GrowthcraftGrapes.active() || !PamFood.installed()) return;
        hide(PamFood.produce("grape"));
        hide(PamFood.seed("grape"));
        Block crop = PamFood.crop("grape");
        if (crop != null) hide(Item.getItemFromBlock(crop));
    }

    private static void hide(Item item) {
        if (item != null) API.hideItem(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
    }

    @Override
    public String getName() {
        return "Adventura Arcana Core food";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
