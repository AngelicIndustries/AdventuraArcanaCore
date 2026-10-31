package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * An Aether parachute follows its rider by reference, not by mounting, so when the rider falls out of the Aether or
 * takes a portal it keeps steering a player in another world. The Aether's config blames this for server crashes. The
 * parachute now goes as soon as its rider is in another world or dead.
 */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.entities.passive.mountable.EntityParachute", remap = false)
public abstract class MixinParachuteRider {

    @Shadow(remap = false)
    private EntityPlayer ridingPlayer;

    @Shadow(remap = false)
    public abstract void die();

    @Inject(method = { "onUpdate", "func_70071_h_" }, at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$stayWithRider(CallbackInfo ci) {
        EntityPlayer rider = ridingPlayer;
        if (rider != null && (rider.isDead || rider.worldObj != ((Entity) (Object) this).worldObj)) {
            die();
            ci.cancel();
        }
    }
}
