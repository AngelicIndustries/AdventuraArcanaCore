package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Falling Et Futurum concrete powder turns into concrete in the first water it reaches, from the falling block's own
 * update, as on modern versions. Every block it passed in a tick is checked, so fast powder can't skip a thin layer of
 * water. Replaces Et Futurum's per-tick tracking of falling powder (see MixinConcreteTracking). Without Et Futurum
 * the blocks aren't there and this does nothing.
 */
@Mixin(EntityFallingBlock.class)
public abstract class MixinFallingConcretePowder extends Entity {

    @Unique
    private static Block adventuraarcanacore$powder, adventuraarcanacore$concrete;

    private MixinFallingConcretePowder(World world) {
        super(world);
    }

    // Right after it moves: if it also lands this tick, vanilla would put the powder in place of the water
    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/item/EntityFallingBlock;moveEntity(DDD)V",
            shift = At.Shift.AFTER),
        cancellable = true,
        require = 1)
    private void adventuraarcanacore$solidifyInWater(CallbackInfo ci) {
        EntityFallingBlock self = (EntityFallingBlock) (Object) this;
        // On its first tick its block is still in place; vanilla removes it after this
        if (worldObj.isRemote || self.field_145812_b <= 1) return;
        if (adventuraarcanacore$powder == null) {
            adventuraarcanacore$powder = Block.getBlockFromName("etfuturum:concrete_powder");
            adventuraarcanacore$concrete = Block.getBlockFromName("etfuturum:concrete");
        }
        Block powder = adventuraarcanacore$powder, concrete = adventuraarcanacore$concrete;
        if (powder == null || powder == Blocks.air
            || concrete == null
            || concrete == Blocks.air
            || self.func_145805_f() != powder) return;
        int x = MathHelper.floor_double(posX);
        int z = MathHelper.floor_double(posZ);
        int from = MathHelper.floor_double(Math.max(prevPosY, posY));
        int to = MathHelper.floor_double(Math.min(prevPosY, posY));
        for (int y = from; y >= to; y--) {
            if (worldObj.getBlock(x, y, z)
                .getMaterial() == Material.water) {
                worldObj.setBlock(x, y, z, concrete, self.field_145814_a, 3);
                setDead();
                ci.cancel();
                return;
            }
        }
    }
}
