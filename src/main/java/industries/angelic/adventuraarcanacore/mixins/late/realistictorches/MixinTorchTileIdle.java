package industries.angelic.adventuraarcanacore.mixins.late.realistictorches;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * Realistic Torches' lit torch keeps a tile entity that looks for rain every tick; TorchClock looks instead, once a
 * second and only while it rains, so the tile entity no longer ticks.
 */
@Pseudo
@Mixin(targets = "com.chaosthedude.realistictorches.blocks.te.TETorch", remap = false)
public abstract class MixinTorchTileIdle extends TileEntity {

    @Override
    public boolean canUpdate() {
        return false;
    }
}
