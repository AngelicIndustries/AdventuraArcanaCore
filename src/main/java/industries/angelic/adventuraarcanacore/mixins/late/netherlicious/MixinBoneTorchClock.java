package industries.angelic.adventuraarcanacore.mixins.late.netherlicious;

import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchClock;
import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchLighting;

/**
 * Netherlicious' bone torches, which never go out on their own, burn on TorchClock's time: placing one starts it.
 * Lighters relight them.
 */
@Pseudo
@Mixin(
    targets = { "DelirusCrux.Netherlicious.Common.Blocks.TorchBone",
        "DelirusCrux.Netherlicious.Common.Blocks.TorchBoneSoul",
        "DelirusCrux.Netherlicious.Common.Blocks.TorchBoneFoxfire",
        "DelirusCrux.Netherlicious.Common.Blocks.TorchBoneShadow" },
    remap = false)
public abstract class MixinBoneTorchClock extends BlockTorch {

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.getBlock(x, y, z) == this) TorchClock.placed(world, x, y, z, this);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        return TorchLighting.light(world, x, y, z, player);
    }
}
