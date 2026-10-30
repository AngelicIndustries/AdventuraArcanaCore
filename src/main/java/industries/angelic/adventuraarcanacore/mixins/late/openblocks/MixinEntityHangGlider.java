package industries.angelic.adventuraarcanacore.mixins.late.openblocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.adventuraarcanacore.compat.openblocks.HangGlider;
import openblocks.common.entity.EntityHangGlider;

/** The hang glider glides about 2.5 blocks per block of height instead of 6, dives slower and wears out in the air. */
@Mixin(value = EntityHangGlider.class, remap = false)
public abstract class MixinEntityHangGlider {

    @ModifyConstant(
        method = { "onUpdate", "func_70071_h_" },
        constant = @Constant(doubleValue = 0.03),
        remap = false,
        require = 1)
    private double adventuraarcanacore$glidePush(double push) {
        return HangGlider.GLIDE_PUSH;
    }

    @ModifyConstant(
        method = { "onUpdate", "func_70071_h_" },
        constant = @Constant(doubleValue = -0.052),
        remap = false,
        require = 1)
    private double adventuraarcanacore$glideSink(double sink) {
        return HangGlider.GLIDE_SINK;
    }

    @ModifyConstant(
        method = { "onUpdate", "func_70071_h_" },
        constant = @Constant(doubleValue = 0.1),
        remap = false,
        require = 1)
    private double adventuraarcanacore$divePush(double push) {
        return HangGlider.DIVE_PUSH;
    }

    @Inject(method = "isGliderValid", at = @At("TAIL"), cancellable = true, remap = false, require = 1)
    private static void adventuraarcanacore$wear(EntityPlayer player, EntityHangGlider glider,
        CallbackInfoReturnable<Boolean> valid, @Local ItemStack held) {
        if (valid.getReturnValueZ() && !HangGlider.wear(player, glider, held, glider.isDeployed()))
            valid.setReturnValue(false);
    }
}
