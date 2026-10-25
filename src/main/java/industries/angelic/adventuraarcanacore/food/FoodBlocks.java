package industries.angelic.adventuraarcanacore.food;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Natural forms of HarvestCraft crops that have none of their own: wild bushes, the pods of Pam-like trees for the tree
 * crops, and fruiting vines. Registered only with HarvestCraft installed, since they yield its food. Biomic grows them
 * wild (compat.biomic.FoodPlants). They came from Biomic, which remaps its old names to them in older worlds.
 */
public final class FoodBlocks {

    /** Tree crop: HarvestCraft fruit texture it is drawn from, ripe colour, log and leaf metadata of its tree. */
    private static final class Pod {

        final String texture;
        final int colour, wood;

        Pod(String texture, int colour, int wood) {
            this.texture = texture;
            this.colour = colour;
            this.wood = wood;
        }
    }

    private static final Map<String, Pod> PODS = new java.util.TreeMap<>();

    static {
        PODS.put("coffeebean", new Pod("date", 0x9B1B1F, 3));
        PODS.put("curryleaf", new Pod("olive", 0x2E1A36, 3));
        PODS.put("spiceleaf", new Pod("pecan", 0x1E263A, 0));
        PODS.put("tealeaf", new Pod("starfruit", 0xF4F2E6, 0));
    }
    private static final String[] VINES = { "grape", "kiwi" };

    private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    private static BlockWildBush bush;
    private static boolean registered;

    private FoodBlocks() {}

    public static synchronized void register() {
        if (registered || !PamFood.installed()) return;
        registered = true;
        bush = new BlockWildBush();
        bush.setCreativeTab(CreativeTabs.tabDecorations);
        GameRegistry.registerBlock(bush, ItemBlockFood.class, "wild_bush");
        BLOCKS.put("wild_bush", bush);
        for (Map.Entry<String, Pod> pod : new java.util.TreeMap<>(PODS).entrySet()) {
            Pod p = pod.getValue();
            BlockFruitPod block = new BlockFruitPod(pod.getKey(), p.texture, p.colour);
            block.setCreativeTab(CreativeTabs.tabDecorations);
            String name = pod.getKey() + "_pod";
            GameRegistry.registerBlock(block, ItemBlockFood.class, name);
            BLOCKS.put(name, block);
        }
        for (String crop : VINES) {
            BlockFruitVine fruiting = new BlockFruitVine(crop, true), bare = new BlockFruitVine(crop, false);
            fruiting.twin = bare;
            bare.twin = fruiting;
            fruiting.setCreativeTab(CreativeTabs.tabDecorations);
            GameRegistry.registerBlock(fruiting, crop + "_vine");
            GameRegistry.registerBlock(bare, crop + "_vine_bare");
            BLOCKS.put(crop + "_vine", fruiting);
            BLOCKS.put(crop + "_vine_bare", bare);
        }
        AdventuraArcanaCore.LOG.info("HarvestCraft food blocks: {}", BLOCKS.keySet());
    }

    /** The wild bush, or null without HarvestCraft. */
    public static Block bush() {
        return bush;
    }

    /** The bush's metadata for a bush crop, or -1. */
    public static int bushMeta(String crop) {
        return BlockWildBush.species(crop);
    }

    /** A fruiting vine for a vine crop, or null. */
    public static Block vine(String crop) {
        return BLOCKS.get(crop + "_vine");
    }

    /** The Pam-like tree of a tree crop, grown by HarvestCraft's fruit-tree generator with its pod, or null. */
    public static WorldGenerator tree(String crop) {
        Block pod = BLOCKS.get(crop + "_pod");
        if (pod == null) return null;
        int wood = PODS.get(crop).wood;
        try {
            Constructor<?> constructor = Class.forName("com.pam.harvestcraft.WorldGenPamFruitTree")
                .getConstructor(boolean.class, int.class, int.class, int.class, Block.class);
            return (WorldGenerator) constructor.newInstance(false, 5, wood, wood, pod);
        } catch (ReflectiveOperationException | LinkageError error) {
            AdventuraArcanaCore.LOG.warn("No HarvestCraft fruit-tree generator for {}: {}", crop, error.toString());
            return null;
        }
    }

    public static boolean isPod(Block block) {
        return block instanceof BlockFruitPod;
    }
}
