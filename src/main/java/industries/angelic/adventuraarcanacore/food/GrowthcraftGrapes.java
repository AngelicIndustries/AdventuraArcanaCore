package industries.angelic.adventuraarcanacore.food;

import net.minecraft.item.Item;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.Config;

/**
 * Growthcraft's grapes as the only grape: the wild grape vines give them and their seeds in place of HarvestCraft's,
 * and NEI hides HarvestCraft's grape, grape seeds and grape crop. Growthcraft's grapes are in the ore dictionary as
 * cropGrape, so HarvestCraft's grape recipes take them.
 */
public final class GrowthcraftGrapes {

    public static final String MOD = "Growthcraft|Grapes";

    private static Item grapes, seeds;
    private static boolean resolved;

    private GrowthcraftGrapes() {}

    /** Whether the switch is on and Growthcraft's grapes are installed. */
    public static boolean active() {
        resolve();
        return grapes != null && seeds != null;
    }

    public static Item grapes() {
        resolve();
        return grapes;
    }

    public static Item seeds() {
        resolve();
        return seeds;
    }

    private static synchronized void resolve() {
        if (resolved) return;
        resolved = true;
        if (!Config.growthcraftGrapes || !Loader.isModLoaded(MOD)) return;
        grapes = GameRegistry.findItem(MOD, "grc.grapes");
        seeds = GameRegistry.findItem(MOD, "grc.grapeSeeds");
    }
}
