package industries.angelic.adventuraarcanacore.mixins.late.distanthorizons;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.seibel.distanthorizons.core.api.internal.ClientApi;

/**
 * Distant Horizons' GTNH builds are dev builds, so it greets every player in chat with a nightly/unstable build
 * warning. The pack picks the build on purpose: skip that message, keep its other boot warnings. Client only.
 */
@Mixin(value = ClientApi.class, remap = false)
public abstract class MixinClientApiDevBuild {

    @Redirect(
        method = "detectAndSendBootTimeWarnings",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETSTATIC,
            target = "Lcom/seibel/distanthorizons/coreapi/ModInfo;IS_DEV_BUILD:Z"),
        require = 1)
    private boolean adventuraarcanacore$notDevBuild() {
        return false;
    }
}
