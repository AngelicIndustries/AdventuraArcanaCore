package industries.angelic.adventuraarcanacore.mixins.late.wildmobs;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Wild Mobs gives a skeleton joining a world a skeleton wolf now and then, without asking which side the world is on.
 * Every skeleton the server sent a client counted as new there, so the client spawned wolves of its own, which the
 * server never knew of and never removed: over a session thousands piled up, ticking and drawn, each holding on to
 * its skeleton. Only the server gives skeletons wolves now; the client sees those as any other mob.
 */
@Pseudo
@Mixin(targets = "com.wildmobsmod.misc.SkeletonWolfHandler", remap = false)
public abstract class MixinSkeletonWolfClientSpawn {

    @Inject(method = "onSkeletonSpawn", at = @At("HEAD"), cancellable = true, remap = false)
    private void adventuraarcanacore$serverOnly(EntityJoinWorldEvent event, CallbackInfo ci) {
        if (event.world.isRemote) ci.cancel();
    }
}
