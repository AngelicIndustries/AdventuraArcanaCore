package industries.angelic.adventuraarcanacore.mixins.late.realistictorches;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchLighting;

/**
 * Realistic Torches lights its torches (unlit, lit or smoldering) with the vanilla flint and steel only, into its own
 * lit torch. TorchLighting lights them with every lighter, each into its own flame.
 */
@Pseudo
@Mixin(targets = "com.chaosthedude.realistictorches.handler.TorchHandler", remap = false)
public abstract class MixinTorchLighting {

    @Inject(method = "lightTorch", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void adventuraarcanacore$moreLighters(World world, int x, int y, int z, EntityPlayer player,
        CallbackInfoReturnable<Boolean> result) {
        if (TorchLighting.light(world, x, y, z, player)) result.setReturnValue(true);
    }
}
