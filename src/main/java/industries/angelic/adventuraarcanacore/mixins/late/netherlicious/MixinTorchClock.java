package industries.angelic.adventuraarcanacore.mixins.late.netherlicious;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchClock;
import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchLighting;

/**
 * Netherlicious' soul, foxfire and shadow torches burn on TorchClock's time: placing one starts it, instead of
 * Netherlicious' own burnout, which removes them and whose updates now do nothing. Lighters relight them.
 */
@Pseudo
@Mixin(
    targets = { "DelirusCrux.Netherlicious.Common.Blocks.TorchSoul",
        "DelirusCrux.Netherlicious.Common.Blocks.TorchFoxfire", "DelirusCrux.Netherlicious.Common.Blocks.TorchShadow" },
    remap = false)
public abstract class MixinTorchClock extends BlockTorch {

    @Inject(
        method = { "onBlockAdded(Lnet/minecraft/world/World;III)V", "func_149726_b(Lnet/minecraft/world/World;III)V" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$startClock(World world, int x, int y, int z, CallbackInfo ci) {
        TorchClock.placed(world, x, y, z, this);
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

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        return TorchLighting.light(world, x, y, z, player);
    }
}
