package industries.angelic.adventuraarcanacore.mixins.late.openblocks;

import net.minecraft.world.World;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

/**
 * OpenBlocks places a grave no higher than its maximalPosY (249 by default, under a 256-block world's top), so in a
 * taller world (Hohenangst's 512) a player who died high up got a grave clamped down to Y=249, often in the air far
 * below. The limit now rises with the world's height, keeping its distance from the top.
 */
@Mixin(targets = "openblocks.common.PlayerDeathHandler$GraveCallable", remap = false)
public abstract class MixinGraveHeight {

    @ModifyExpressionValue(
        method = "findLocation(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lopenblocks/common/PlayerDeathHandler$GravePlacementChecker;)Lopenmods/utils/Coord;",
        at = @At(value = "FIELD", target = "Lopenblocks/Config;maxGraveY:I", opcode = Opcodes.GETSTATIC),
        remap = false,
        require = 2)
    private int adventuraarcanacore$worldTop(int maxGraveY, @Local(argsOnly = true) World world) {
        return maxGraveY + Math.max(0, world.getHeight() - 256);
    }
}
