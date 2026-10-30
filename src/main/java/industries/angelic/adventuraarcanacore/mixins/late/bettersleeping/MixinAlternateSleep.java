package industries.angelic.adventuraarcanacore.mixins.late.bettersleeping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;

import cz.ondraster.bettersleeping.logic.AlternateSleep;

/**
 * Better Sleeping's "players are sleeping" message rounds the sleeping share down before turning it into a percentage,
 * so it always says 0%. Show the whole percentage of the players it counted.
 */
@Mixin(value = AlternateSleep.class, remap = false)
public abstract class MixinAlternateSleep {

    @ModifyArg(
        method = "trySleepingWorld(Lnet/minecraft/world/World;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/ChatComponentTranslation;<init>(Ljava/lang/String;[Ljava/lang/Object;)V"),
        index = 1,
        remap = false,
        require = 1)
    private static Object[] adventuraarcanacore$wholePercent(Object[] args, @Local(index = 2) int sleeping,
        @Local(index = 3) int total) {
        return new Object[] { sleeping * 100 / Math.max(total, 1) };
    }
}
