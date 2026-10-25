package industries.angelic.adventuraarcanacore.compartment;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;

public final class Compartments {

    public static final int GUI_ID = 10;
    public static final BlockCompartment BLOCK = new BlockCompartment();

    public static void preInit() {
        GameRegistry.registerBlock(BLOCK, ItemCompartment.class, "compartment");
        GameRegistry.registerTileEntity(TileCompartment.class, "adventuraarcanacore:compartment");
        CompartmentNetwork.init();
    }

    public static void registerRecipes() {
        recipe(CompartmentTier.WOOD, Items.book, Blocks.chest, Blocks.stone_button);
        recipe(
            CompartmentTier.COPPER,
            stack(CompartmentTier.WOOD),
            material("Copper", "ingotIron"),
            Blocks.stone_button);
        recipe(
            CompartmentTier.BRONZE,
            stack(CompartmentTier.COPPER),
            material("Bronze", "ingotGold"),
            Items.gold_nugget);
        recipe(CompartmentTier.IRON, stack(CompartmentTier.COPPER), material("Iron", "ingotIron"), Items.gold_nugget);
        recipe(CompartmentTier.GOLD, stack(CompartmentTier.IRON), material("Gold", "ingotGold"), Items.emerald);
        recipe(CompartmentTier.DIAMOND, stack(CompartmentTier.GOLD), "gemDiamond", Items.emerald);
    }

    private static String material(String metal, String fallback) {
        if (!OreDictionary.getOres("gear" + metal)
            .isEmpty()) return "gear" + metal;
        if (!OreDictionary.getOres("ingot" + metal)
            .isEmpty()) return "ingot" + metal;
        return fallback;
    }

    private static void recipe(CompartmentTier tier, Object center, Object material, Object corner) {
        GameRegistry
            .addRecipe(new ShapedOreRecipe(stack(tier), "pcp", "cbc", "pcp", 'b', center, 'c', material, 'p', corner));
    }

    public static ItemStack stack(CompartmentTier tier) {
        return new ItemStack(BLOCK, 1, tier.ordinal());
    }

}
