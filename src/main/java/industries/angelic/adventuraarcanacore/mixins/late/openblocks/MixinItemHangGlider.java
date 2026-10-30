package industries.angelic.adventuraarcanacore.mixins.late.openblocks;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.compat.openblocks.HangGlider;
import openblocks.common.item.ItemHangGlider;

/** The hang glider wears out (see MixinEntityHangGlider) and is repaired with leather on an anvil. */
@Mixin(ItemHangGlider.class)
public abstract class MixinItemHangGlider extends Item {

    @Inject(method = "<init>", at = @At("RETURN"), remap = false, require = 1)
    private void adventuraarcanacore$wearsOut(CallbackInfo ci) {
        setMaxDamage(HangGlider.DURABILITY);
        setMaxStackSize(1);
    }

    @Override
    public boolean getIsRepairable(ItemStack glider, ItemStack material) {
        return material.getItem() == Items.leather;
    }
}
