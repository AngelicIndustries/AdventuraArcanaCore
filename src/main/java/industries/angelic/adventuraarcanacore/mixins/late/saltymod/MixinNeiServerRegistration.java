package industries.angelic.adventuraarcanacore.mixins.late.saltymod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** SaltyMod registers its NEI screens on a dedicated server too, where they do not exist. Dedicated server only. */
@Pseudo
@Mixin(targets = "darkbum.saltymod.api.nei.NEIConfig", remap = false)
public abstract class MixinNeiServerRegistration {

    @Inject(method = "loadConfig", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void adventuraarcanacore$skipClientScreens(CallbackInfo callback) {
        callback.cancel();
    }
}
