package industries.angelic.adventuraarcanacore.compat.mowziesmobs;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Mowzie's Mobs' blowgun is a bow in code, so while it is blown vanilla holds it in the bow's drawing pose, angled
 * across the screen. It is held level from the mouth along the view instead. Client only.
 */
public final class BlowgunAim {

    private static Item blowgun;
    private static boolean looked;

    private BlowgunAim() {}

    /** Whether the local player is blowing this stack's blowgun. */
    public static boolean blowing(ItemStack stack) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        return stack != null && player != null && player.getItemInUseCount() > 0 && stack.getItem() == blowgun();
    }

    /**
     * Replaces the held item's first-person placement, just before ItemRenderer draws it: the matrix it pushed for the
     * item is reset to the one under it (the camera with the arm's sway), then the tube is laid level from in front of
     * the mouth toward the crosshair. Measured in camera space; ItemRenderer.renderItem's own item placement is undone
     * at the end, as it is applied next.
     */
    public static void aim() {
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        // The texture's bottom-left end, the mouthpiece, sits just below and in front of the eye.
        GL11.glTranslatef(0.0F, -0.12F, -0.1F);
        // The icon's diagonal points straight ahead, with the icon lying flat under the view.
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        // 1.2 blocks long, corner to corner.
        float scale = 1.2F / (float) Math.sqrt(2.0D);
        GL11.glScalef(scale, scale, scale);
        // renderItemIn2D mirrors the icon: its bottom-left corner is (1, 0), halfway through its thickness.
        GL11.glTranslatef(-1.0F, 0.0F, 0.03125F);
        // Undo ItemRenderer.renderItem's 2D item placement, which follows.
        GL11.glTranslatef(0.9375F, 0.0625F, 0.0F);
        GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.0F / 1.5F, 1.0F / 1.5F, 1.0F / 1.5F);
        GL11.glTranslatef(0.0F, 0.3F, 0.0F);
    }

    private static Item blowgun() {
        if (!looked) {
            blowgun = GameRegistry.findItem("mowziesmobs", "blowgun");
            looked = true;
        }
        return blowgun;
    }
}
