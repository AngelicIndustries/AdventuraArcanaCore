package industries.angelic.adventuraarcanacore.mixins.late.twilightforest;

import org.spongepowered.asm.mixin.Mixin;

import twilightforest.tileentity.TileEntityTFFirefly;

/**
 * Renders a firefly in both world passes: its body before water as before, its glow in the translucent pass after
 * water (MixinFireflyRendererGlow). Drawn with the solid pass, the glow wrote depth before the water did, so water
 * behind it was skipped and the glow showed over the bare background: a dark halo cut into the water.
 */
@Mixin(value = TileEntityTFFirefly.class, remap = false)
public abstract class MixinFireflyRenderPasses {

    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }
}
