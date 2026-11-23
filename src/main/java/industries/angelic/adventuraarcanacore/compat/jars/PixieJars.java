package industries.angelic.adventuraarcanacore.compat.jars;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.core.PatchSwitches;

/**
 * Critter jars that act alike. Biomes O' Plenty's pixie jar can be set down (pixieJarPlacing). Sneak-using it, or
 * Twilight Forest's firefly jar (fireflyJarRelease), lets the critter out, uncorked. BOP is reached by reflection;
 * Twilight Forest is a compile dependency.
 */
public final class PixieJars {

    /** BOP's filled jar is jarFilled; meta 2 holds a pixie. */
    static final int PIXIE_META = 2;
    static final String UNCORK = AdventuraArcanaCore.MODID + ":block.jar.uncork";
    /**
     * Biomes O' Plenty's own pixie sound, by reference (its license allows no altered copy), muffled by the glass at
     * play time with Sound Physics.
     */
    static final String PIXIE_SOUND = AdventuraArcanaCore.MODID + ":block.jar.pixie";

    public static BlockPixieJar pixieJar;
    private static Item bopJarFilled;

    private PixieJars() {}

    public static void preInit() {
        boolean pixies = Loader.isModLoaded("BiomesOPlenty") && PatchSwitches.enabled("pixieJarPlacing");
        boolean fireflies = Loader.isModLoaded("TwilightForest") && PatchSwitches.enabled("fireflyJarRelease");
        if (pixies) {
            pixieJar = new BlockPixieJar();
            GameRegistry.registerBlock(pixieJar, (Class<? extends ItemBlock>) null, "pixie_jar");
            GameRegistry.registerTileEntity(TilePixieJar.class, AdventuraArcanaCore.MODID + ":pixie_jar");
        }
        if (pixies || fireflies) MinecraftForge.EVENT_BUS.register(new JarReleases(pixies, fireflies));
    }

    /** Colors the pixie's light pink with Supernova. */
    public static void init() {
        if (pixieJar == null || !Loader.isModLoaded("supernova")) return;
        try {
            Method register = Class.forName("com.mitchej123.supernova.api.LightColorRegistry")
                .getMethod("register", Block.class, int.class, int.class, int.class);
            // At the jar's light (8): Supernova only scales a color up to a block's light, never down
            register.invoke(null, pixieJar, 8, 4, 7);
        } catch (ReflectiveOperationException | LinkageError e) {
            AdventuraArcanaCore.LOG.warn("Couldn't color the pixie jar's light with Supernova", e);
        }
    }

    /** Muffles the pixie in a jar with Sound Physics, as heard through the glass. Client only. */
    public static void muffleSound() {
        try {
            Class.forName("com.sonicether.soundphysics.SoundPhysics")
                .getMethod("muffleSound", String.class, float.class)
                .invoke(null, PIXIE_SOUND, 0.9F);
        } catch (ReflectiveOperationException | LinkageError e) {
            AdventuraArcanaCore.LOG.warn("Couldn't muffle pixie jars with Sound Physics", e);
        }
    }

    /** BOP's filled jar item, looked up once it exists. */
    public static Item bopJarFilled() {
        if (bopJarFilled == null) bopJarFilled = GameRegistry.findItem("BiomesOPlenty", "jarFilled");
        return bopJarFilled;
    }

    /** A BOP jar with a pixie in it, or null without BOP. */
    static ItemStack filledJar() {
        Item jar = bopJarFilled();
        return jar == null ? null : new ItemStack(jar, 1, PIXIE_META);
    }

    /**
     * A pixie's sparkle trail, through BOP's proxy. Its spawnParticle compares the name with ==, which works because
     * string literals are interned across the whole game.
     */
    static void trail(double x, double y, double z) {
        try {
            Object proxy = Class.forName("biomesoplenty.BiomesOPlenty")
                .getField("proxy")
                .get(null);
            proxy.getClass()
                .getMethod("spawnParticle", String.class, double.class, double.class, double.class, Object[].class)
                .invoke(proxy, "pixietrail", x, y, z, new Object[0]);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // No sparkle then
        }
    }
}
