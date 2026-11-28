package industries.angelic.adventuraarcanacore.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cpw.mods.fml.client.SplashProgress;
import cpw.mods.fml.common.FMLLog;

@Mixin(value = SplashProgress.class, remap = false)
public abstract class MixinSplashProgressKeepEnabled {

    /**
     * Forge calls this when loading fails, writing enabled=false to splash.properties. Returning false skips the write;
     * finish() still throws the loading error.
     */
    @Inject(method = "disableSplash", at = @At("HEAD"), cancellable = true)
    private static void adventuraarcanacore$keepSplashEnabled(CallbackInfoReturnable<Boolean> cir) {
        FMLLog.info("Adventura Arcana Core: left the splash screen enabled in config/splash.properties");
        cir.setReturnValue(false);
    }
}
