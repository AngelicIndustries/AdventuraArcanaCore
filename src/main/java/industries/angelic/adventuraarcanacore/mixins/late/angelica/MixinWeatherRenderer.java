package industries.angelic.adventuraarcanacore.mixins.late.angelica;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.angelica.render.WeatherRenderer;

import industries.angelic.adventuraarcanacore.compat.angelica.AngelicaDSWeather;

/**
 * With instanced weather on, Angelica cancels EntityRenderer.addRainParticles and renderRainSnow from their heads.
 * Dynamic Surroundings plays its rain sounds and splashes in the first and draws its auroras in the second, so both
 * went missing. Hand the particles back to Dynamic Surroundings and draw its auroras after Angelica's rain.
 * Not @Pseudo: {@link AngelicaDSWeather#verify} fails at startup if this didn't apply.
 */
@Mixin(value = WeatherRenderer.class, remap = false)
public abstract class MixinWeatherRenderer implements AngelicaDSWeather.Patched {

    @Inject(method = "spawnRainParticles()Z", at = @At("HEAD"), cancellable = true)
    private static void adventuraarcanacore$leaveParticlesToDS(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "render(F)Z", at = @At("RETURN"))
    private static void adventuraarcanacore$renderDSAuroras(float partialTicks, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) AngelicaDSWeather.renderAuroras(partialTicks);
    }
}
