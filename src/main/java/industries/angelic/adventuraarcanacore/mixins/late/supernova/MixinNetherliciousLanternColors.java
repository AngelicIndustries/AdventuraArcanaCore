package industries.angelic.adventuraarcanacore.mixins.late.supernova;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.compat.netherlicious.NetherliciousLanterns;

/**
 * Supernova gives Netherlicious' fire lanterns white light and its lit redstone lanterns none of their own, so they
 * light white too. NetherliciousLanterns sets theirs as Supernova finishes Netherlicious', before
 * supernova-colors.cfg is read.
 */
@Pseudo
@Mixin(targets = "com.mitchej123.supernova.compat.colors.NetherliciousColors", remap = false)
public abstract class MixinNetherliciousLanternColors {

    @Inject(method = "register", at = @At("TAIL"), remap = false, require = 1)
    private static void adventuraarcanacore$lanterns(CallbackInfo ci) {
        NetherliciousLanterns.registerColors();
    }
}
