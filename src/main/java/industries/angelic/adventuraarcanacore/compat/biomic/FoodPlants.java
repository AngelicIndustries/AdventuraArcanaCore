package industries.angelic.adventuraarcanacore.compat.biomic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.DoubleToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.Loader;
import industries.angelic.adventuraarcanacore.food.FoodBlocks;
import industries.angelic.adventuraarcanacore.food.PamFood;
import industries.angelic.biomic.api.EcologyOverlays;
import industries.angelic.biomic.api.ForeignGenerators;
import industries.angelic.biomic.api.PlantLayer;

/**
 * HarvestCraft's food growing wild in Biomic's habitats (ecology proposal v3): the habitats' food lists
 * ({@code food.json}, an overlay of Biomic's palettes) and what each name grows as in Biomic's plant layer. Biomic
 * grows
 * it, so HarvestCraft's own gardens, fruit trees, hives and salt stand down in Biomic's worlds; Salty owns salt.
 */
final class FoodPlants {

    private static final String OVERLAY = "/assets/adventuraarcanacore/biomic/ecology/food.json";
    /** Crops of sandy coasts; every other crop needs grass or dirt. */
    private static final Set<String> SAND_CROPS = new HashSet<>(Arrays.asList("asparagus", "radish", "candleberry"));
    /** Palms and dry-country fruit trees, which root in sand. */
    private static final Set<String> SAND_TREES = new HashSet<>(
        Arrays
            .asList("date", "coconut", "fig", "pomegranate", "pistachio", "almond", "olive", "cashew", "dragonfruit"));

    private FoodPlants() {}

    /** At preInit: the food lists and the generators that stand down. */
    static void preInit() {
        if (!PamFood.installed()) return;
        EcologyOverlays.register("palettes.json", OVERLAY);
        for (String generator : new String[] { "PamGardenGenerator", "PamTreeGenerator", "PamBeeGenerator",
            "WorldGenPamSalt" })
            ForeignGenerators.standDown("com.pam.harvestcraft." + generator, ForeignGenerators.Scope.EVERY_DIMENSION);
        // Natura's overworld pass (wild barley, cotton and berry bushes): HarvestCraft's grow there instead. The Nether
        // keeps Natura's generator.
        ForeignGenerators.standDown("mods.natura.worldgen.BaseCropWorldgen", ForeignGenerators.Scope.OVERWORLD);
    }

    /** At postInit, once the food blocks exist: what the names grow as. */
    static void postInit() {
        if (!PamFood.installed()) return;
        PlantLayer.crops(crop -> {
            Block block = PamFood.crop(crop);
            if (block == null) return null;
            // Wild plants are not all at one stage: most ripe, some still growing (HarvestCraft crop metadata 3 to 7).
            return patch(
                block,
                roll -> roll < 0.55D ? 7 : roll < 0.7D ? 6 : roll < 0.85D ? 5 : roll < 0.93D ? 4 : 3,
                SAND_CROPS.contains(crop),
                crop.equals("whitemushroom"));
        });
        PlantLayer.bushes(crop -> {
            Block bush = FoodBlocks.bush();
            int meta = FoodBlocks.bushMeta(crop);
            return bush == null || meta < 0 ? null : patch(bush, roll -> meta, SAND_CROPS.contains(crop), false);
        });
        PlantLayer.trees(fruit -> {
            WorldGenerator pam = PamFood.fruitTree(fruit);
            WorldGenerator generator = pam != null ? pam : FoodBlocks.tree(fruit);
            if (generator == null) return null;
            boolean sand = SAND_TREES.contains(fruit);
            return new PlantLayer.Tree() {

                @Override
                public WorldGenerator generator() {
                    return generator;
                }

                @Override
                public boolean sand() {
                    return sand;
                }

                /** HarvestCraft plants its fruit unripe; at generation most of it is ripe. */
                @Override
                public int ripen(Block block, int metadata, double roll) {
                    if (metadata != 0 || !fruit(block)) return metadata;
                    return roll < 0.6D ? 2 : roll < 0.85D ? 1 : 0;
                }
            };
        });
        PlantLayer.vines(FoodBlocks::vine);
    }

    /** EFR's bee nests on the habitats' trees, the only wild hives (EFR comes with Biomic). */
    static void nests() {
        if (!Loader.isModLoaded("etfuturum")) return;
        PlantLayer.nest(new PlantLayer.Nest() {

            @Override
            public boolean available() {
                return BeeNests.available();
            }

            @Override
            public String state(int facing) {
                return BeeNests.state(facing);
            }

            @Override
            public NBTTagCompound payload(int x, int y, int z, int facing, long hash) {
                return BeeNests.payload(x, y, z, facing, hash);
            }
        });
    }

    private static boolean fruit(Block block) {
        return FoodBlocks.isPod(block) || "com.pam.harvestcraft.BlockPamFruit".equals(
            block.getClass()
                .getName());
    }

    private static PlantLayer.Patch patch(Block block, DoubleToIntFunction stage, boolean sand, boolean ring) {
        return new PlantLayer.Patch() {

            @Override
            public Block block() {
                return block;
            }

            @Override
            public int metadata(double roll) {
                return stage.applyAsInt(roll);
            }

            @Override
            public boolean sand() {
                return sand;
            }

            @Override
            public boolean ring() {
                return ring;
            }
        };
    }
}
