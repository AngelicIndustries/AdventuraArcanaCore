package industries.angelic.adventuraarcanacore.compat.jars;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import twilightforest.block.BlockTFFireflyJar;
import twilightforest.block.TFBlocks;

/**
 * Twilight Forest's firefly jar as an item, drawn as pixie jars and jars of eyes are: the jar, with its fireflies
 * glowing inside as they do in a jar that's set down, each lighting up somewhere, fading and lighting up elsewhere.
 */
public class FireflyJarItemRenderer implements IItemRenderer {

    private static final ResourceLocation GLOW = new ResourceLocation("twilightforest:textures/model/firefly-tiny.png");
    /** The glow's ten pixels in its texture. */
    private static final float U0 = 30 / 64.0F, U1 = 40 / 64.0F, V0 = 0, V1 = 10 / 32.0F;
    private static final int FIREFLIES = 3;
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    private final Random random = new Random();

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY
            || type == ItemRenderType.EQUIPPED
            || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return helper == ItemRendererHelper.INVENTORY_BLOCK || helper == ItemRendererHelper.ENTITY_ROTATION
            || helper == ItemRendererHelper.ENTITY_BOBBING;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        boolean inventory = type == ItemRenderType.INVENTORY;
        GL11.glPushMatrix();
        if (inventory || type == ItemRenderType.ENTITY) GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        JarModel.renderAlone(
            (RenderBlocks) data[0],
            TFBlocks.fireflyJar,
            BlockTFFireflyJar.jarSide,
            BlockTFFireflyJar.jarTop,
            BlockTFFireflyJar.jarCork);
        drawFireflies(PixieJarRenderer.itemTime(), System.identityHashCode(stack), inventory);
        GL11.glPopMatrix();
    }

    /**
     * The fireflies, as the jar's own: each glows up and fades over a second or so, somewhere in the jar and of some
     * size, then does again elsewhere.
     */
    private void drawFireflies(float time, long seed, boolean inventory) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(GLOW);
        float lastX = OpenGlHelper.lastBrightnessX;
        float lastY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        for (int i = 0; i < FIREFLIES; i++) {
            // Each its own rhythm, between one and a half seconds and two
            int life = 30 + i * 5;
            float t = time + i * 13 + (seed & 255);
            long round = (long) Math.floor(t / life);
            float phase = (t - round * life) / life;
            float brightness = phase < 0.5F ? phase * 2 : (1 - phase) * 2;
            random.setSeed(seed * 31 + i * 7919L + round * 104729L);
            // Where Twilight Forest's jar puts them
            float x = 0.5F + (random.nextFloat() - random.nextFloat()) * 0.25F;
            float y = 0.4F + (random.nextFloat() - random.nextFloat()) * 0.35F;
            float z = 0.5F + (random.nextFloat() - random.nextFloat()) * 0.25F;
            float size = (0.2F + random.nextFloat() * 0.6F) * 0.3125F;
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, z);
            billboard(inventory);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, brightness);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-size, -size, 0, U0, V1);
            tessellator.addVertexWithUV(size, -size, 0, U1, V1);
            tessellator.addVertexWithUV(size, size, 0, U1, V0);
            tessellator.addVertexWithUV(-size, size, 0, U0, V0);
            tessellator.draw();
            GL11.glPopMatrix();
        }
        GL11.glPopAttrib();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
    }

    /** Turns what's drawn next to face the eye, keeping the scale; slots are drawn upside down. */
    private static void billboard(boolean flipY) {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        float size = (float) Math
            .sqrt(MATRIX.get(0) * MATRIX.get(0) + MATRIX.get(1) * MATRIX.get(1) + MATRIX.get(2) * MATRIX.get(2));
        for (int i : new int[] { 0, 1, 2, 4, 5, 6, 8, 9, 10 }) MATRIX.put(i, 0);
        MATRIX.put(0, size);
        MATRIX.put(5, flipY ? -size : size);
        MATRIX.put(10, size);
        MATRIX.rewind();
        GL11.glLoadMatrix(MATRIX);
    }
}
