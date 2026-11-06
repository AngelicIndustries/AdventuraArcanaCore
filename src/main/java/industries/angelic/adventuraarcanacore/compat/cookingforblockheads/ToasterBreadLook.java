package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * SaltyMod's salted bread drawn without its salt particles while the toaster draws its slots. Client only; the item
 * keeps its icon everywhere else.
 */
public final class ToasterBreadLook {

    private static IIcon saltlessBread;
    private static Item saltedBread;
    private static boolean resolved, drawing;

    @SubscribeEvent
    public void stitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 1) return;
        saltlessBread = PaintedSprite
            .register(event.map, "adventuraarcanacore:toaster_salt_bread", ToastPaint::saltlessBread);
    }

    public static void drawing(boolean now) {
        drawing = now;
    }

    /** The toaster's icon for this item and metadata, or null for its own. */
    public static IIcon icon(Item item, int meta) {
        if (!drawing || meta != 0) return null;
        if (!resolved) {
            saltedBread = GameRegistry.findItem("saltymod", "salt_bread");
            resolved = true;
        }
        return item == saltedBread ? saltlessBread : null;
    }
}
