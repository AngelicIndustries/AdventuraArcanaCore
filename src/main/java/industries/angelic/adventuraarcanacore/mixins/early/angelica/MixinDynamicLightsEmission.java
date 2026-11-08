package industries.angelic.adventuraarcanacore.mixins.early.angelica;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.angelica.dynamiclights.DynamicLights;

import industries.angelic.adventuraarcanacore.compat.angelica.EmissionOverrides;

/** The luminance /lightdebughand sets for an item, held or dropped, in place of the one Angelica works out. */
@Mixin(value = DynamicLights.class, remap = false)
public abstract class MixinDynamicLightsEmission {

    @Inject(method = "getLuminanceFromItemStack", at = @At("HEAD"), cancellable = true, require = 1)
    private static void adventuraarcanacore$override(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        Integer level = EmissionOverrides.item(stack);
        if (level != null) cir.setReturnValue(level);
    }
}
