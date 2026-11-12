package industries.angelic.adventuraarcanacore.mixins.late.twilightforest;

import org.spongepowered.asm.mixin.Mixin;

import twilightforest.entity.passive.EntityTFMobileFirefly;
import twilightforest.entity.passive.EntityTFTinyFirefly;

/**
 * The glowing fireflies (the tiny ones around firefly blocks and jars, and the ambient mobile ones) render in the
 * translucent pass, after water, instead of the solid pass, where their glow wrote depth and cut dark squares into the
 * water behind them (MixinTinyFireflyDepth draws them without depth).
 */
@Mixin(value = { EntityTFTinyFirefly.class, EntityTFMobileFirefly.class }, remap = false)
public abstract class MixinTinyFireflyPass {

    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}
