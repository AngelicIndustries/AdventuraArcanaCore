package industries.angelic.adventuraarcanacore.compat.realistictorches;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * Glowstone dust worked into clay: crafted onto a lit Realistic Torches torch, it makes a vanilla torch, which never
 * goes out. After the glowstone paste of later Realistic Torches versions. Painted from vanilla's clay ball in
 * glowstone's colours (TorchPaint).
 */
public class ItemGlowstonePaste extends Item {

    public ItemGlowstonePaste() {
        setUnlocalizedName(AdventuraArcanaCore.MODID + ".glowstone_paste");
        setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = PaintedSprite.register(register, AdventuraArcanaCore.MODID + ":glowstone_paste", TorchPaint::paste);
    }
}
