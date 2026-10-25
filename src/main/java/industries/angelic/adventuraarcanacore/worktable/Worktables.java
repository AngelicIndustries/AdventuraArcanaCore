package industries.angelic.adventuraarcanacore.worktable;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;

public final class Worktables {

    public static final int GUI_ID = 11;
    public static final BlockWorktable BLOCK = new BlockWorktable();

    private Worktables() {}

    public static void preInit() {
        GameRegistry.registerBlock(BLOCK, "worktable");
        GameRegistry.registerTileEntity(TileWorktable.class, "adventuraarcanacore:worktable");
        WorktableNetwork.init();
    }

    public static void registerRecipes() {
        Object table = OreDictionary.getOres("craftingTableWood")
            .isEmpty() ? Blocks.crafting_table : "craftingTableWood";
        Object chest = OreDictionary.getOres("chestWood")
            .isEmpty() ? Blocks.chest : "chestWood";
        GameRegistry.addRecipe(
            new ShapedOreRecipe(new ItemStack(BLOCK), "B", "W", "C", 'B', Items.book, 'W', table, 'C', chest));
    }
}
