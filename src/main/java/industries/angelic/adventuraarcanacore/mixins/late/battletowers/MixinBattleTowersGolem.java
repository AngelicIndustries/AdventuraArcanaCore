package industries.angelic.adventuraarcanacore.mixins.late.battletowers;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.adventuraarcanacore.compat.battletowers.BattleTowers;

/**
 * A Battle Tower collapses when its golem dies, in six explosions from the golem's floor down, unless the golem stood
 * at y=50 or below: vanilla's depth for an underground tower's golem, which guards its bottom floor. In tall worlds
 * those golems stand far above y=50, so the explosions went off where the player stood and in the rock below. A golem
 * with its tower's floors above it now keeps its tower standing, as at vanilla depths.
 */
@Pseudo
@Mixin(targets = "atomicstryker.battletowers.common.AS_EntityGolem", remap = false)
public abstract class MixinBattleTowersGolem {

    @Shadow(remap = false)
    private int towerX;

    @Shadow(remap = false)
    private int towerY;

    @Shadow(remap = false)
    private int towerZ;

    @ModifyConstant(
        method = { "onDeath", "func_70645_a" },
        constant = @Constant(intValue = 50),
        remap = false,
        require = 1)
    private int adventuraarcanacore$collapseAbove(int original) {
        return BattleTowers.buried(((Entity) (Object) this).worldObj, towerX, towerY, towerZ) ? Integer.MAX_VALUE
            : original;
    }
}
