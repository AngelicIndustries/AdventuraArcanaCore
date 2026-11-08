package industries.angelic.adventuraarcanacore.mixins.late.supernova;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.Config;

/**
 * Supernova fades a light's color toward white the dimmer it is: its hue confidence, level / (60 - 3 level), leaves a
 * redstone torch (level 7) 18% of its red and half of any color at level 12. coloredLightStrength picks a flatter
 * curve; "supernova" keeps Supernova's. Read per call, so a change shows once chunks rebuild (F3+A).
 */
@Pseudo
@Mixin(targets = "com.mitchej123.supernova.client.TintBlendMode", remap = false)
public abstract class MixinSupernovaColorStrength {

    @Inject(method = "hueConfidence", at = @At("HEAD"), cancellable = true, require = 1)
    private static void adventuraarcanacore$strength(float light, CallbackInfoReturnable<Float> cir) {
        int curve = Config.coloredLightStrengthCurve;
        if (curve == Config.COLOR_STRENGTH_SUPERNOVA) return;
        float level = Math.min(15, Math.max(0, light)) / 15;
        if (curve == Config.COLOR_STRENGTH_LINEAR) cir.setReturnValue(level);
        else if (curve == Config.COLOR_STRENGTH_STRONG) cir.setReturnValue((float) Math.sqrt(level));
        else cir.setReturnValue(level > 0 ? 1f : 0f);
    }
}
