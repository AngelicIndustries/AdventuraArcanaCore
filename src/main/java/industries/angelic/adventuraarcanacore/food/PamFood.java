package industries.angelic.adventuraarcanacore.food;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * HarvestCraft and Harvest the Nether for the wild food (ecology proposal v3): their own crop blocks, fruit trees
 * and items, looked up by registry name. Both mods are optional; their classes are only touched when installed.
 */
public final class PamFood {

    public static final String MOD = "harvestcraft";
    public static final String NETHER = "harvestthenether";
    /** The HarvestCraft build the food is written against. */
    public static final String SUPPORTED = "1.3.13-GTNH";

    /** Seed items whose names do not follow HarvestCraft's {@code <crop>seedItem} pattern. */
    private static final Map<String, String> SEEDS = new java.util.HashMap<>();
    private static final Map<String, String> PRODUCE = new java.util.HashMap<>();

    static {
        SEEDS.put("coffeebean", "coffeeseedItem");
        SEEDS.put("tealeaf", "teaseedItem");
        SEEDS.put("mustardseeds", "mustardseedItem");
        SEEDS.put("sesameseeds", "sesameseedsseedItem");
        SEEDS.put("sunflower", "");
        PRODUCE.put("sunflower", "sunflowerseedsItem");
    }

    private static final Map<String, Optional<WorldGenerator>> TREES = new ConcurrentHashMap<>();
    private static volatile Boolean installed;

    private PamFood() {}

    /** HarvestCraft at the version the food is written against. */
    public static boolean installed() {
        Boolean known = installed;
        if (known == null) {
            ModContainer mod = Loader.instance()
                .getIndexedModList()
                .get(MOD);
            known = mod != null && SUPPORTED.equals(mod.getVersion());
            if (mod != null && !known) AdventuraArcanaCore.LOG
                .warn("HarvestCraft {} is not {}; no wild HarvestCraft food", mod.getVersion(), SUPPORTED);
            installed = known;
        }
        return known;
    }

    public static boolean netherInstalled() {
        return Loader.isModLoaded(NETHER);
    }

    /** HarvestCraft's crop block for a crop (garlic: pamgarlicCrop), or null. */
    public static Block crop(String crop) {
        return installed() ? block(MOD, "pam" + crop + "Crop") : null;
    }

    public static Block block(String mod, String name) {
        if (!Loader.isModLoaded(mod)) return null;
        Block block = GameRegistry.findBlock(mod, name);
        return block == null || block == Blocks.air ? null : block;
    }

    /** The generator HarvestCraft's own sapling of this fruit grows (apple: pamappleSapling), or null. */
    public static WorldGenerator fruitTree(String fruit) {
        if (!installed()) return null;
        return TREES.computeIfAbsent(fruit, key -> {
            Block sapling = block(MOD, "pam" + key + "Sapling");
            if (sapling == null) return Optional.empty();
            try {
                Field field = sapling.getClass()
                    .getDeclaredField("treeGen");
                field.setAccessible(true);
                return Optional.ofNullable((WorldGenerator) field.get(sapling));
            } catch (ReflectiveOperationException | RuntimeException error) {
                AdventuraArcanaCore.LOG.warn("No HarvestCraft tree generator for {}: {}", key, error.toString());
                return Optional.empty();
            }
        })
            .orElse(null);
    }

    /** The food a crop yields (garlic: garlicItem), or null. */
    public static Item produce(String crop) {
        return item(PRODUCE.getOrDefault(crop, crop + "Item"));
    }

    /** The seed a crop is sown from, or null when it has none. */
    public static Item seed(String crop) {
        String name = SEEDS.getOrDefault(crop, crop + "seedItem");
        return name.isEmpty() ? null : item(name);
    }

    private static Item item(String name) {
        return installed() ? GameRegistry.findItem(MOD, name) : null;
    }
}
