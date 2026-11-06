package industries.angelic.adventuraarcanacore.mixins.late.realistictorches;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchClock;

/**
 * Realistic Torches' lit and smoldering torches burn on TorchClock's time instead of their own: placing one starts it,
 * and their scheduled updates, which smolder and put them out, do nothing.
 */
@Pseudo
@Mixin(
    targets = { "com.chaosthedude.realistictorches.blocks.BlockTorchLit",
        "com.chaosthedude.realistictorches.blocks.BlockTorchSmoldering" },
    remap = false)
public abstract class MixinTorchClock {

    @Inject(
        method = { "onBlockAdded(Lnet/minecraft/world/World;III)V", "func_149726_b(Lnet/minecraft/world/World;III)V" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$startClock(World world, int x, int y, int z, CallbackInfo ci) {
        TorchClock.placed(world, x, y, z, (Block) (Object) this);
        ci.cancel();
    }

    @Inject(
        method = { "updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
            "func_149674_a(Lnet/minecraft/world/World;IIILjava/util/Random;)V" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$ownTicks(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        ci.cancel();
    }
}
