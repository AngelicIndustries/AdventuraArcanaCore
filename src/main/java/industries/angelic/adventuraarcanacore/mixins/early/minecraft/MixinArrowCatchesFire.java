package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * An arrow flying through fire or lava catches fire, as on modern versions; 1.7.10 only lights arrows from a Flame bow.
 * The whole path of the tick is checked, so a fast arrow can't skip a single fire block.
 */
@Mixin(EntityArrow.class)
public abstract class MixinArrowCatchesFire extends Entity {

    private MixinArrowCatchesFire(World world) {
        super(world);
    }

    // After the in-flight move and its block collisions; a stuck arrow doesn't get here
    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/EntityArrow;func_145775_I()V",
            shift = At.Shift.AFTER),
        require = 1)
    private void adventuraarcanacore$catchFire(CallbackInfo ci) {
        if (worldObj.isRemote || isBurning()) return;
        double dx = posX - prevPosX, dy = posY - prevPosY, dz = posZ - prevPosZ;
        int steps = 1 + (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 4);
        for (int k = steps - 1; k >= 0; k--) {
            double back = (double) k / steps;
            AxisAlignedBB box = boundingBox.getOffsetBoundingBox(-dx * back, -dy * back, -dz * back)
                .contract(0.001D, 0.001D, 0.001D);
            if (worldObj.func_147470_e(box)) {
                // As long as fire lights anything that walks through it
                setFire(8);
                return;
            }
        }
    }
}
