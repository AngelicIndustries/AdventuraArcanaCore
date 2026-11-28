package industries.angelic.adventuraarcanacore.compat.netherlicious;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The item of Netherlicious' lanterns that have a plain ItemBlock: the same, and worn in a belt slot. The Efrine ones
 * keep their own item, which MixinEfrineLanternBauble makes a belt bauble.
 */
public class LanternBaubleItem extends ItemBlock implements IBaubleExpanded {

    public LanternBaubleItem(Block block) {
        super(block);
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return NetherliciousLanterns.BAUBLE_TYPES;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.BELT;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase wearer) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase wearer) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        BaubleItemHelper.addSlotInformation(tooltip, NetherliciousLanterns.BAUBLE_TYPES);
    }
}
