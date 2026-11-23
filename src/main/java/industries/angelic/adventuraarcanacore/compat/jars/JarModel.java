package industries.angelic.adventuraarcanacore.compat.jars;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

/** Twilight Forest's firefly jar: a glass body and a cork, drawn as two boxes. */
final class JarModel {

    private JarModel() {}

    /** In the world, as part of its chunk. */
    static void renderInWorld(RenderBlocks renderer, Block block, IIcon cork, int x, int y, int z) {
        renderer.setRenderBounds(0.1875, 0.0, 0.1875, 0.8125, 0.875, 0.8125);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setOverrideBlockTexture(cork);
        renderer.setRenderBounds(0.25, 0.75, 0.25, 0.75, 1.0, 0.75);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.clearOverrideBlockTexture();
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
    }

    /** On its own, filling the unit cube from the origin, lit as items are. */
    static void renderAlone(RenderBlocks renderer, Block block, IIcon side, IIcon top, IIcon cork) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        renderer.setRenderBounds(0.1875, 0.0, 0.1875, 0.8125, 0.875, 0.8125);
        box(renderer, block, side, top);
        renderer.setRenderBounds(0.25, 0.75, 0.25, 0.75, 1.0, 0.75);
        box(renderer, block, cork, cork);
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
        GL11.glPopAttrib();
    }

    private static void box(RenderBlocks renderer, Block block, IIcon side, IIcon top) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0, -1, 0);
        renderer.renderFaceYNeg(block, 0, 0, 0, top);
        tessellator.setNormal(0, 1, 0);
        renderer.renderFaceYPos(block, 0, 0, 0, top);
        tessellator.setNormal(0, 0, -1);
        renderer.renderFaceZNeg(block, 0, 0, 0, side);
        tessellator.setNormal(0, 0, 1);
        renderer.renderFaceZPos(block, 0, 0, 0, side);
        tessellator.setNormal(-1, 0, 0);
        renderer.renderFaceXNeg(block, 0, 0, 0, side);
        tessellator.setNormal(1, 0, 0);
        renderer.renderFaceXPos(block, 0, 0, 0, side);
        tessellator.draw();
    }
}
