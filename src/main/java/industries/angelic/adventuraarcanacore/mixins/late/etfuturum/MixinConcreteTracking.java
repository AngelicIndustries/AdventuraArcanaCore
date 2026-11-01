package industries.angelic.adventuraarcanacore.mixins.late.etfuturum;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.core.handlers.ServerEventHandler;

/**
 * Et Futurum keeps every falling concrete powder in a set and checks the set for water once per world tick. Placing
 * the concrete could add powder to the set mid-loop and crash the server (ConcurrentModificationException), fast
 * powder over deep water made two concrete, and powder whose chunk unloaded stayed in the set. The falling block now
 * solidifies itself (MixinFallingConcretePowder), so the set stays empty and the loop is off. Neither target exists
 * once Et Futurum does the same itself, so both are optional.
 */
@Mixin(value = ServerEventHandler.class, remap = false)
public abstract class MixinConcreteTracking {

    @Redirect(
        method = "spawnEvent(Lnet/minecraftforge/event/entity/EntityJoinWorldEvent;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"),
        require = 0)
    private boolean adventuraarcanacore$dontTrack(Set<Object> tracked, Object fallingPowder) {
        return false;
    }

    @Inject(method = "doConcreteTracking", at = @At("HEAD"), cancellable = true, require = 0)
    private void adventuraarcanacore$noLoop(CallbackInfo ci) {
        ci.cancel();
    }
}
