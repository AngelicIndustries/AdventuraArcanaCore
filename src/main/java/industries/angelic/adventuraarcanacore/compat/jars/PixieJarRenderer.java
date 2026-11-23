package industries.angelic.adventuraarcanacore.compat.jars;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Draws pixie jars: the jar with its chunk (Twilight Forest's two boxes, glass and cork), the pixie inside as a tile
 * entity, and Biomes O' Plenty's pixie jar item the same way. The pixie is one of BOP's own, never spawned, drawn by
 * its own renderer as a mob spawner draws its mob, so its wings beat and it glows as a free pixie does.
 */
public class PixieJarRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

    /** The pixie's size in a slot, against its size in a jar. */
    private static final float SCALE_GUI = 1.6F;

    private EntityLivingBase pixie;
    private boolean pixieFailed;

    /* The jar, set down */

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        JarModel.renderInWorld(renderer, block, ((BlockPixieJar) block).cork, x, y, z);
        return true;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return BlockPixieJar.renderId;
    }

    /* The pixie in a jar that's set down */

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TilePixieJar jar)) return;
        float time = jar.age + partialTicks;
        float[] at = PixieFlight.at(time, jar.seed());
        GL11.glPushMatrix();
        GL11.glTranslated(x + at[0], y + at[1], z + at[2]);
        drawPixie(tile.getWorldObj(), time, at[3], 1.0F);
        GL11.glPopMatrix();
    }

    /* BOP's pixie jar as an item */

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return stack.getItemDamage() == PixieJars.PIXIE_META && PixieJars.pixieJar != null
            && (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY
                || type == ItemRenderType.EQUIPPED
                || type == ItemRenderType.EQUIPPED_FIRST_PERSON);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return helper == ItemRendererHelper.INVENTORY_BLOCK || helper == ItemRendererHelper.ENTITY_ROTATION
            || helper == ItemRendererHelper.ENTITY_BOBBING;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPixieJar jar = PixieJars.pixieJar;
        GL11.glPushMatrix();
        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        mc.getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        JarModel.renderAlone((RenderBlocks) data[0], jar, jar.side, jar.top, jar.cork);
        if (mc.theWorld != null) {
            float time = itemTime();
            float[] at = PixieFlight.at(time, System.identityHashCode(stack));
            if (type == ItemRenderType.INVENTORY) {
                // In a slot the jar is a few pixels across: a larger pixie hovering in the middle, or it's a speck
                GL11.glTranslatef(0.5F, at[1] - 0.06F, 0.5F);
                drawPixie(mc.theWorld, time, at[3], SCALE_GUI);
            } else {
                GL11.glTranslatef(at[0], at[1], at[2]);
                drawPixie(mc.theWorld, time, at[3], 1.0F);
            }
            // As the inventory screen does after drawing the player: slots are drawn without the lightmap
            if (type == ItemRenderType.INVENTORY) {
                OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
        GL11.glPopMatrix();
    }

    /** Smooth time for items, which have no partial tick of their own. */
    static float itemTime() {
        return (Minecraft.getSystemTime() % 3_600_000L) / 50.0F;
    }

    /* The pixie */

    /**
     * Draws the pixie with its feet at the origin, facing the yaw, by BOP's renderer: lit, then its glow. The scale is
     * on top of its size in a jar.
     */
    private void drawPixie(World world, float time, float yaw, float scale) {
        EntityLivingBase pixie = pixie(world);
        if (pixie == null) return;
        Render render = RenderManager.instance.getEntityRenderObject(pixie);
        if (render == null || RenderManager.instance.renderEngine == null) return;
        int ticks = MathHelper.floor_float(time);
        pixie.ticksExisted = ticks;
        pixie.renderYawOffset = pixie.prevRenderYawOffset = yaw;
        pixie.rotationYawHead = pixie.prevRotationYawHead = yaw;
        pixie.rotationYaw = pixie.prevRotationYaw = yaw;
        float lastX = OpenGlHelper.lastBrightnessX;
        float lastY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_LIGHTING_BIT
                | GL11.GL_CURRENT_BIT);
        GL11.glPushMatrix();
        float size = PixieFlight.SCALE * scale;
        GL11.glScalef(size, size, size);
        render.doRender(pixie, 0, 0, 0, yaw, time - ticks);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
    }

    /** BOP's pixie for this world, made once; it's never spawned or ticked. */
    private EntityLivingBase pixie(World world) {
        if (world == null || pixieFailed) return null;
        if (pixie == null) {
            try {
                pixie = (EntityLivingBase) Class.forName("biomesoplenty.common.entities.EntityPixie")
                    .getConstructor(World.class)
                    .newInstance(world);
            } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
                pixieFailed = true;
                AdventuraArcanaCore.LOG.warn("Couldn't make a Biomes O' Plenty pixie for pixie jars", e);
                return null;
            }
        } else if (pixie.worldObj != world) {
            pixie.setWorld(world);
        }
        return pixie;
    }
}
