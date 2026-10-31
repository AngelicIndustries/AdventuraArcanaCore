package industries.angelic.adventuraarcanacore.mixins.late.battletowers;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.adventuraarcanacore.compat.battletowers.BattleTowers;

/** Battle Towers' builder: the site checks find the ground, and the floors reach above it, at the world's height. */
@Pseudo
@Mixin(targets = "atomicstryker.battletowers.common.AS_WorldGenTower", remap = false)
public abstract class MixinBattleTowersGenerator {

    @ModifyConstant(method = "getSurfaceBlockHeight", constant = @Constant(intValue = 50), remap = false, require = 1)
    private int adventuraarcanacore$searchStart(int original, @Local(argsOnly = true) World world) {
        return BattleTowers.searchStart(world);
    }

    /** Both the floors' upper bound and the short-tower floor numbering compare against y=120. */
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 120), remap = false, require = 2)
    private int adventuraarcanacore$towerTop(int original, @Local(argsOnly = true) World world,
        @Local(argsOnly = true, ordinal = 1) int surface) {
        return BattleTowers.towerTop(world, surface);
    }
}
