package industries.angelic.adventuraarcanacore.compat.netherlicious;

import net.minecraft.block.Block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.compat.supernova.SupernovaColors;

/**
 * Netherlicious' lanterns, every one a LanternBase that hangs at metadata 1: worn in a Baubles Expanded belt slot like
 * Et Futurum's, hung from the belt by Even Mo' Bends, and lit by Supernova in their flame's color. Reached by name, as
 * AAC doesn't build against Netherlicious or Even Mo' Bends.
 */
public final class NetherliciousLanterns {

    public static final String[] BAUBLE_TYPES = { "belt" };

    private static final String MOD = "netherlicious";
    private static final String LANTERN_BASE = "DelirusCrux.Netherlicious.Common.Blocks.Lantern.LanternBase";
    private static final int HANGING = 1;
    /** Fire lanterns, warm like Et Futurum's lantern; Supernova lights them white. */
    private static final String[] FIRE = { "Lantern", "LanternGold", "LanternEfrine" };
    /** Lit redstone lanterns, red like Supernova's redstone torch (6, 1, 1) at full light; it has none for them. */
    private static final String[] REDSTONE = { "RedstoneLanternOn", "RedstoneLanternGoldOn",
        "RedstoneLanternEfrineOn" };

    private static Class<?> base;

    private NetherliciousLanterns() {}

    /**
     * Whether this Netherlicious block is a lantern that can be worn on the belt. Redstone lanterns can't: a lit one
     * drops as the unlit block, so worn it would never light.
     */
    public static boolean isWearable(Block block) {
        return base().isInstance(block) && !block.getClass()
            .getSimpleName()
            .startsWith("LanternRedstone");
    }

    /** Has Even Mo' Bends hang worn lanterns from the belt, when a version with its worn lantern API is installed. */
    public static void registerWithMoBends() {
        if (!Loader.isModLoaded("mobends")) return;
        try {
            Class.forName("net.gobbob.mobends.api.WornLanterns")
                .getMethod("registerHangingBlock", Class.class, int.class)
                .invoke(null, base(), HANGING);
        } catch (ReflectiveOperationException e) {
            AdventuraArcanaCore.LOG.info("This Even Mo' Bends doesn't hang worn lanterns; Netherlicious' won't show");
        }
    }

    /** Called where Supernova sets Netherlicious' colors, so supernova-colors.cfg, read next, still overrides these. */
    public static void registerColors() {
        for (String name : FIRE) color(name, 15, 12, 5);
        for (String name : REDSTONE) color(name, 15, 3, 3);
    }

    private static void color(String name, int r, int g, int b) {
        Block block = GameRegistry.findBlock(MOD, name);
        if (block != null) SupernovaColors.register(block, -1, r, g, b);
    }

    private static Class<?> base() {
        if (base == null) {
            try {
                base = Class.forName(LANTERN_BASE);
            } catch (ClassNotFoundException e) {
                base = Void.class;
            }
        }
        return base;
    }
}
