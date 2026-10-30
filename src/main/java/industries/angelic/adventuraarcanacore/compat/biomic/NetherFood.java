package industries.angelic.adventuraarcanacore.compat.biomic;

import java.lang.reflect.Constructor;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.biomic.api.EcologyOverlays;
import industries.angelic.biomic.api.ForeignGenerators;
import industries.angelic.biomic.api.PlantLayer;

/**
 * Harvest the Nether in Biomic's Tartarus (ecology proposal v3): its crops grow wild in patches on nether ground in
 * the biomes {@code nether.json} names, glow flowers drift over Soul Valley's soul sand, and its ignis trees stand here
 * and there. Its own generators stand down: they sample only the lower 128 blocks and ignore the biomes.
 */
final class NetherFood {

    private static final String MOD = "harvestthenether";
    private static final String IGNIS = MOD + ":ignisFruit";
    private static WorldGenerator ignisTree;
    private static boolean ignisResolved;

    private NetherFood() {}

    static void preInit() {
        if (!Loader.isModLoaded(MOD)) return;
        EcologyOverlays.register("palettes.json", "/assets/adventuraarcanacore/biomic/ecology/nether.json");
        ForeignGenerators
            .standDown("com.pam.harvestthenether.PamGardenGenerator", ForeignGenerators.Scope.EVERY_DIMENSION);
        ForeignGenerators
            .standDown("com.pam.harvestthenether.PamTreeGenerator", ForeignGenerators.Scope.EVERY_DIMENSION);
    }

    static void postInit() {
        if (!Loader.isModLoaded(MOD)) return;
        PlantLayer.netherPlants(id -> {
            if (!id.startsWith(MOD + ":") || id.equals(IGNIS)) return null;
            Block block = block(id);
            if (block == null) return null;
            // Crops wild and ripe; the glow flower (a nether garden) roots in soul sand and netherrack only.
            int metadata = id.endsWith("Crop") ? 7 : 0;
            boolean garden = block.getClass()
                .getSimpleName()
                .equals("BlockNetherGarden");
            return new PlantLayer.Patch() {

                @Override
                public Block block() {
                    return block;
                }

                @Override
                public int metadata(double roll) {
                    return metadata;
                }

                @Override
                public boolean grows(Block ground) {
                    return !garden || ground == Blocks.soul_sand || ground == Blocks.netherrack;
                }
            };
        });
        PlantLayer.netherTrees(id -> {
            if (!id.equals(IGNIS)) return null;
            WorldGenerator generator = ignisTree();
            return generator == null ? null : () -> generator;
        });
    }

    private static Block block(String id) {
        Block block = GameRegistry.findBlock(MOD, id.substring(MOD.length() + 1));
        return block == null || block == Blocks.air ? null : block;
    }

    private static synchronized WorldGenerator ignisTree() {
        if (!ignisResolved) {
            ignisResolved = true;
            Block fruit = block(IGNIS);
            if (fruit != null) {
                try {
                    Constructor<?> constructor = Class.forName("com.pam.harvestthenether.WorldGenNetherTree")
                        .getConstructor(boolean.class, int.class, int.class, int.class, Block.class);
                    ignisTree = (WorldGenerator) constructor.newInstance(true, 5, 3, 3, fruit);
                } catch (ReflectiveOperationException | LinkageError error) {
                    AdventuraArcanaCore.LOG.warn("No Harvest the Nether ignis tree: {}", error.toString());
                }
            }
        }
        return ignisTree;
    }
}
