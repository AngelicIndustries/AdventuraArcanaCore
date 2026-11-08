package industries.angelic.adventuraarcanacore.compat.supernova;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import cpw.mods.fml.common.Loader;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Supernova's block light colors, reached by reflection as AAC doesn't build against Supernova: the color a block
 * emits, registering one, reloading supernova-colors.cfg and relighting chunks. Every call is a no-op (or null) without
 * Supernova.
 */
public final class SupernovaColors {

    private static final MethodHandle EMISSION, RED, GREEN, BLUE, REGISTER_ALL, REGISTER_META, RELOAD, REBUILD,
        LIGHT_MANAGER, RELIGHT;

    static {
        MethodHandle emission = null, red = null, green = null, blue = null, registerAll = null, registerMeta = null,
            reload = null, rebuild = null, lightManager = null, relight = null;
        if (Loader.isModLoaded("supernova")) {
            try {
                MethodHandles.Lookup lookup = MethodHandles.publicLookup();
                Class<?> registry = Class.forName("com.mitchej123.supernova.api.LightColorRegistry");
                Class<?> packed = Class.forName("com.mitchej123.supernova.api.PackedColorLight");
                Class<?> manager = Class.forName("com.mitchej123.supernova.light.WorldLightManager");
                emission = lookup.findStatic(
                    registry,
                    "getPackedEmissionNoWorld",
                    MethodType.methodType(int.class, Block.class, int.class));
                red = lookup.findStatic(packed, "red", MethodType.methodType(int.class, int.class));
                green = lookup.findStatic(packed, "green", MethodType.methodType(int.class, int.class));
                blue = lookup.findStatic(packed, "blue", MethodType.methodType(int.class, int.class));
                registerAll = lookup.findStatic(
                    registry,
                    "register",
                    MethodType.methodType(void.class, Block.class, int.class, int.class, int.class));
                registerMeta = lookup.findStatic(
                    registry,
                    "register",
                    MethodType.methodType(void.class, Block.class, int.class, int.class, int.class, int.class));
                reload = lookup.findStatic(
                    Class.forName("com.mitchej123.supernova.config.BlockColorConfig"),
                    "reload",
                    MethodType.methodType(int.class, File.class));
                rebuild = lookup.findStatic(
                    Class.forName("com.mitchej123.supernova.light.LightRegistries"),
                    "rebuildCaches",
                    MethodType.methodType(void.class));
                lightManager = lookup.findVirtual(
                    Class.forName("com.mitchej123.supernova.world.SupernovaWorld"),
                    "supernova$getLightManager",
                    MethodType.methodType(manager));
                relight = lookup.findVirtual(
                    manager,
                    "forceRelightChunk",
                    MethodType.methodType(boolean.class, int.class, int.class));
            } catch (ReflectiveOperationException e) {
                AdventuraArcanaCore.LOG.warn("Supernova's light color API changed; light colors stay as they are", e);
                emission = null;
            }
        }
        EMISSION = emission;
        RED = red;
        GREEN = green;
        BLUE = blue;
        REGISTER_ALL = registerAll;
        REGISTER_META = registerMeta;
        RELOAD = reload;
        REBUILD = rebuild;
        LIGHT_MANAGER = lightManager;
        RELIGHT = relight;
    }

    private SupernovaColors() {}

    public static boolean available() {
        return EMISSION != null;
    }

    /** The R, G and B (0 to 15) the block emits at this metadata, or null without Supernova. */
    public static int[] emission(Block block, int meta) {
        if (!available()) return null;
        try {
            int packed = (int) EMISSION.invokeExact(block, meta);
            return new int[] { (int) RED.invokeExact(packed), (int) GREEN.invokeExact(packed),
                (int) BLUE.invokeExact(packed) };
        } catch (Throwable t) {
            return null;
        }
    }

    /** Registers a color for every metadata (meta below 0) or one, and refreshes Supernova's caches. */
    public static void register(Block block, int meta, int r, int g, int b) {
        if (!available()) return;
        try {
            if (meta < 0) REGISTER_ALL.invokeExact(block, r, g, b);
            else REGISTER_META.invokeExact(block, meta, r, g, b);
            REBUILD.invokeExact();
        } catch (Throwable t) {
            AdventuraArcanaCore.LOG.warn("Couldn't register a light color for {}", block, t);
        }
    }

    /** Re-reads config/supernova-colors.cfg into the registry, as /supernova colors reload does. */
    public static void reload() {
        if (!available()) return;
        try {
            int ignored = (int) RELOAD.invokeExact(
                Loader.instance()
                    .getConfigDir());
            REBUILD.invokeExact();
        } catch (Throwable t) {
            AdventuraArcanaCore.LOG.warn("Couldn't reload supernova-colors.cfg", t);
        }
    }

    /** Relights the chunks within radius of a block, so placed lights take a new color. Call on the world's thread. */
    public static void relight(World world, int blockX, int blockZ, int radius) {
        if (!available() || world == null) return;
        try {
            Object manager = LIGHT_MANAGER.invoke(world);
            if (manager == null) return;
            for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++) {
                boolean ignored = (boolean) RELIGHT.invoke(manager, (blockX >> 4) + dx, (blockZ >> 4) + dz);
            }
        } catch (Throwable t) {
            AdventuraArcanaCore.LOG.warn("Couldn't relight around {} {}", blockX, blockZ, t);
        }
    }
}
