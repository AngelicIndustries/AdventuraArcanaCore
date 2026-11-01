package industries.angelic.adventuraarcanacore.mixins.late.battletowers;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.adventuraarcanacore.compat.battletowers.BattleTowers;

/** Battle Towers' builder: the site checks find the ground from the top down, and the floors reach above it. */
@Pseudo
@Mixin(targets = "atomicstryker.battletowers.common.AS_WorldGenTower", remap = false)
public abstract class MixinBattleTowersGenerator {

    @Inject(method = "getSurfaceBlockHeight", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$surface(World world, int x, int z, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BattleTowers.surface(world, x, z));
    }

    /** Both the floors' upper bound and the short-tower floor numbering compare against y=120. */
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 120), remap = false, require = 2)
    private int adventuraarcanacore$towerTop(int original, @Local(argsOnly = true) World world,
        @Local(argsOnly = true, ordinal = 1) int surface) {
        return BattleTowers.towerTop(world, surface);
    }
}
