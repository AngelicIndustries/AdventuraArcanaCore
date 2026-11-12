package industries.angelic.adventuraarcanacore.mixins.late.twilightforest;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import twilightforest.client.model.ModelTFFirefly;
import twilightforest.client.renderer.TileEntityTFFireflyRenderer;

/**
 * Splits a firefly's rendering between the world passes (MixinFireflyRenderPasses): the body in the solid pass, the
 * glow in the translucent pass after water, without writing depth so it blends over whatever lies behind. The glow is
 * lit as a light source, full block light and no sky light: shaders light the quad from its lightmap, and outdoors the
 * sky light tinted it the sky's blue. Outside the world passes (pass -1) both draw as before.
 */
@Mixin(value = TileEntityTFFireflyRenderer.class, remap = false)
public abstract class MixinFireflyRendererGlow {

    /** Full block light, no sky light, in lightmap coordinates. */
    @Unique
    private static final float adventuraarcanacore$FULL_BLOCK = 240, adventuraarcanacore$NO_SKY = 0;

    @Redirect(
        method = "renderTileEntityFireflyAt",
        at = @At(value = "INVOKE", target = "Ltwilightforest/client/model/ModelTFFirefly;render(F)V"),
        require = 1)
    private void adventuraarcanacore$body(ModelTFFirefly model, float scale) {
        if (MinecraftForgeClient.getRenderPass() != 1) model.render(scale);
    }

    @Redirect(
        method = "renderTileEntityFireflyAt",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V", remap = true),
        require = 1)
    private void adventuraarcanacore$glow(ModelRenderer glow, float scale) {
        int pass = MinecraftForgeClient.getRenderPass();
        if (pass == 0) return;
        if (pass != 1) {
            glow.render(scale);
            return;
        }
        float block = OpenGlHelper.lastBrightnessX, sky = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(
            OpenGlHelper.lightmapTexUnit,
            adventuraarcanacore$FULL_BLOCK,
            adventuraarcanacore$NO_SKY);
        GL11.glDepthMask(false);
        glow.render(scale);
        GL11.glDepthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, block, sky);
    }
}
