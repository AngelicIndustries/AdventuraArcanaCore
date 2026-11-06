package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A burning arrow, or any burning projectile, sets off the TNT minecart it hits, harder the faster it flies, as on
 * modern versions; 1.7.10 treats it like any other arrow.
 */
@Mixin(EntityMinecart.class)
public abstract class MixinMinecartTNTBurningArrow extends Entity {

    private MixinMinecartTNTBurningArrow(World world) {
        super(world);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), require = 1)
    private void adventuraarcanacore$explodeOnBurningArrow(DamageSource source, float amount,
        CallbackInfoReturnable<Boolean> cir) {
        if (worldObj.isRemote || isDead || !((Object) this instanceof EntityMinecartTNT) || !source.isProjectile())
            return;
        Entity projectile = source.getSourceOfDamage();
        if (projectile == null || !projectile.isBurning()) return;
        double speedSquared = projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY
            + projectile.motionZ * projectile.motionZ;
        // Sets the cart dead, so vanilla's damage handling that follows does nothing
        ((AccessorMinecartTNT) this).adventuraarcanacore$explode(speedSquared);
    }
}
