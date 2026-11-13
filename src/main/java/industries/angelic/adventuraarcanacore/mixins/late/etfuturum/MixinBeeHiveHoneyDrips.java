package industries.angelic.adventuraarcanacore.mixins.late.etfuturum;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import ganymedes01.etfuturum.blocks.BlockBeeHive;
import industries.angelic.adventuraarcanacore.Config;

/**
 * A full bee nest or beehive drips honey on 70% of its display ticks, when a random float beats 0.3, as on modern
 * versions; next to a nest that is about three drips a second. Only Config.beeHoneyDrips of them still fall.
 */
@Mixin(value = BlockBeeHive.class, remap = false)
public abstract class MixinBeeHiveHoneyDrips {

    /** EFR's chance that a display tick drips. */
    @Unique
    private static final float adventuraarcanacore$EFR_DRIP = 0.7f;

    @ModifyConstant(
        method = { "randomDisplayTick", "func_149734_b" },
        constant = @Constant(floatValue = 0.3f),
        remap = false,
        require = 1)
    private float adventuraarcanacore$fewerDrips(float noDrip) {
        return 1 - adventuraarcanacore$EFR_DRIP * (float) Config.beeHoneyDrips;
    }
}
