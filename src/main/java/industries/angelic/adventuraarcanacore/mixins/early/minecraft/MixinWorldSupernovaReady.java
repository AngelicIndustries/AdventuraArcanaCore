package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Supernova takes light updates in a world only once it is constructed, marked by a hook on every World constructor
 * ({@code <init>*}). Its 0.1.2 refmap ties that hook to the client constructor alone, so the server's worlds never
 * take a light update: lights placed are never lit on the server, and clients see only what they light themselves.
 * Marks the worlds built with the other constructor, as Supernova means to. Applied after Supernova (higher priority)
 * to reach the field it adds.
 */
@Mixin(value = World.class, priority = 1100)
public abstract class MixinWorldSupernovaReady {

    @Shadow(remap = false)
    private boolean supernova$ready;

    @Inject(
        method = "<init>(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;)V",
        at = @At("RETURN"))
    private void adventuraarcanacore$supernovaReady(CallbackInfo ci) {
        supernova$ready = true;
    }
}
