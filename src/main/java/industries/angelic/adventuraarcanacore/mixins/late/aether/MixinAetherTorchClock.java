package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchClock;
import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchLighting;

/**
 * The Aether's ambrosium, hellfire and ethereal torches, which burn forever, burn on TorchClock's time: placing one
 * starts it, and it goes out into Realistic Torches' unlit torch. Lighters relight them.
 */
@Pseudo
@Mixin(
    targets = { "com.gildedgames.the_aether.blocks.decorative.BlockAmbrosiumTorch",
        "com.gildedgames.the_aether.blocks.decorative.BlockInfernoTorch",
        "com.gildedgames.the_aether.blocks.BlockEtherealTorch" },
    remap = false)
public abstract class MixinAetherTorchClock extends BlockTorch {

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
