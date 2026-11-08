package industries.angelic.adventuraarcanacore.compat.angelica;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.ReflectionHelper;
import industries.angelic.adventuraarcanacore.compat.supernova.SupernovaColors;

/**
 * Light levels the /lightdebug commands give for this game only. A block's is its type's light value, so every block
 * of that type emits it, re-registered with Supernova in the color it had so the level holds; an item's replaces the
 * luminance Angelica's dynamic lights give it, held or dropped (MixinDynamicLightsEmission).
 */
public final class EmissionOverrides {

    private static final int FULL = 15;
    /** Each overridden block type's light value and Supernova color before the first override. */
    private static final Map<Block, Original> blocks = new HashMap<>();
    /** By item name and damage ("name@damage"); read on the chunk build threads. */
    private static final Map<String, Integer> items = new ConcurrentHashMap<>();

    private EmissionOverrides() {}

    private static final class Original {

        final int light;
        final int[] color;

        Original(int light, int[] color) {
            this.light = light;
            this.color = color;
        }
    }

    /** The luminance set for this stack, or null to leave Angelica's. */
    public static Integer item(ItemStack stack) {
        if (items.isEmpty() || stack == null || stack.getItem() == null) return null;
        return items.get(key(stack));
    }

    /**
     * Sets the stack's luminance, or with no level toggles it: off if set, else full. Returns what it is now, null for
     * Angelica's own.
     */
    public static Integer toggleItem(ItemStack stack, Integer level) {
        String key = key(stack);
        if (level == null && items.containsKey(key)) {
            items.remove(key);
            return null;
        }
        int set = level == null ? FULL : level;
        items.put(key, set);
        return set;
    }

    /**
     * Sets the block type's light level, or with no level toggles it: back to its own if set, else full. The color is
     * the one Supernova gives it at this metadata (white for a block that gave no light), re-registered at the new
     * level. Returns the level now, null once back to its own.
     */
    public static Integer toggleBlock(Block block, int meta, Integer level) {
        Original original = blocks.get(block);
        if (level == null && original != null) {
            setLight(block, original.light);
            if (original.color != null)
                SupernovaColors.register(block, -1, original.color[0], original.color[1], original.color[2]);
            blocks.remove(block);
            return null;
        }
        int[] color = SupernovaColors.emission(block, meta);
        if (original == null) blocks.put(block, new Original(block.getLightValue(), color));
        int set = level == null ? FULL : level;
        setLight(block, set);
        if (color != null) {
            int[] scaled = scaled((color[0] | color[1] | color[2]) == 0 ? new int[] { FULL, FULL, FULL } : color, set);
            SupernovaColors.register(block, -1, scaled[0], scaled[1], scaled[2]);
        }
        return set;
    }

    /** The color with its brightest channel at the level: Supernova raises a dimmer color to it, but never lowers. */
    private static int[] scaled(int[] color, int level) {
        int max = Math.max(color[0], Math.max(color[1], color[2]));
        int[] out = new int[3];
        for (int i = 0; i < 3; i++) out[i] = Math.round(color[i] * level / (float) max);
        return out;
    }

    public static boolean overridden(Block block) {
        return blocks.containsKey(block);
    }

    private static void setLight(Block block, int level) {
        ReflectionHelper.setPrivateValue(Block.class, block, level, "lightValue", "field_149784_t");
    }

    private static String key(ItemStack stack) {
        return Item.itemRegistry.getNameForObject(stack.getItem()) + "@" + stack.getItemDamage();
    }
}
