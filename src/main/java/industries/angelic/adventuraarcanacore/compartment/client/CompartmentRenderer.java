package industries.angelic.adventuraarcanacore.compartment.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import industries.angelic.adventuraarcanacore.compartment.CompartmentTier;
import industries.angelic.adventuraarcanacore.compartment.TileCompartment;

public class CompartmentRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

    private final ModelCompartment model = new ModelCompartment();
    private final ResourceLocation[] textures = new ResourceLocation[CompartmentTier.values().length];

    public CompartmentRenderer() {
        for (CompartmentTier tier : CompartmentTier.values()) {
            textures[tier.ordinal()] = new ResourceLocation(
                "adventuraarcanacore",
                "textures/compartment/" + tier.texture + ".png");
        }
    }

    private void render(CompartmentTier tier, double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        bindTexture(textures[tier.ordinal()]);
        GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
        GL11.glRotatef(180, 0, 0, 1);
        model.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
        render(((TileCompartment) tile).getTier(), x, y, z);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        boolean entity = type == ItemRenderType.ENTITY;
        render(CompartmentTier.fromMetadata(item.getItemDamage()), entity ? -0.5 : 0, 0, entity ? -0.5 : 0);
    }
}
