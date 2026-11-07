package industries.angelic.adventuraarcanacore.compat.realistictorches;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * AAC's lighter for ambrosium flame, beside the Aether's Zanite and Cinerarium (hellfire) and Zanite and Cyro (cold
 * fire): an ambrosium shard and a zanite gemstone. It lights torches (TorchLighting) and nothing else. Painted from
 * Zanite and Cinerarium in ambrosium's colours (TorchPaint).
 */
public class ItemZaniteAndAmbrosium extends Item {

    public ItemZaniteAndAmbrosium() {
        setUnlocalizedName(AdventuraArcanaCore.MODID + ".zanite_and_ambrosium");
        setMaxStackSize(1);
        setMaxDamage(24);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = PaintedSprite
            .register(register, AdventuraArcanaCore.MODID + ":zanite_and_ambrosium", TorchPaint::zaniteAndAmbrosium);
    }
}
