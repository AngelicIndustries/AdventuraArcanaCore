package industries.angelic.adventuraarcanacore.mixins.late.twilightforest;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import twilightforest.client.renderer.entity.RenderTFTinyFirefly;
import twilightforest.entity.passive.EntityTFTinyFirefly;

/** Draws a firefly's glow without writing depth, so it blends over the water behind it (MixinTinyFireflyPass). */
@Mixin(value = RenderTFTinyFirefly.class, remap = false)
public abstract class MixinTinyFireflyDepth {

    @Inject(method = "doRenderTinyFirefly", at = @At("HEAD"), require = 1)
    private void adventuraarcanacore$noDepth(EntityTFTinyFirefly firefly, double x, double y, double z,
        float brightness, float size, CallbackInfo ci) {
        GL11.glDepthMask(false);
    }

    @Inject(method = "doRenderTinyFirefly", at = @At("RETURN"), require = 1)
    private void adventuraarcanacore$depthBack(EntityTFTinyFirefly firefly, double x, double y, double z,
        float brightness, float size, CallbackInfo ci) {
        GL11.glDepthMask(true);
    }
}
