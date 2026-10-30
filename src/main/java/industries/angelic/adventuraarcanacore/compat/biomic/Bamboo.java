package industries.angelic.adventuraarcanacore.compat.biomic;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import industries.angelic.biomic.api.BlockSubstitutes;
import industries.angelic.biomic.api.EcologyOverlays;
import industries.angelic.biomic.api.ForeignGenerators;
import industries.angelic.biomic.api.PlantLayer;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.LanguageRegistry;
import industries.angelic.adventuraarcanacore.Config;
import industries.angelic.adventuraarcanacore.food.BambooShoots;
import industries.angelic.adventuraarcanacore.food.BambooSpecies;
import industries.angelic.adventuraarcanacore.food.BlockBambooShoot;

/**
 * The pack's bamboo in Biomic's worlds (review, 2026-10-25): Origen's bamboo in Plant Mega Pack's species, Regions
 * Unexplored's bamboo tree as Madake in both of the Bamboo Forest's tree groups, and a shoot for each bamboo where it
 * grows. Without these Biomic draws its bamboo with Et Futurum's, and the tree under Regions Unexplored's name.
 */
final class Bamboo {

    private static final String ECOLOGY = "/assets/adventuraarcanacore/biomic/ecology/";
    /** Madake's names over Regions Unexplored's for Biomic's two bamboo tree blocks, per language. */
    private static final String[][] MADAKE_NAMES = { { "en_US", "Madake Leaves", "Madake Bamboo" },
        { "ja_JP", "真竹の葉", "真竹" }, { "ru_RU", "Листья мадакэ", "Бамбук мадакэ" },
        { "uk_UA", "Листя мадаке", "Бамбук мадаке" }, { "zh_CN", "桂竹叶", "桂竹" } };

    private Bamboo() {}

    /** At preInit: the overlays and the generator that stands down. */
    static void preInit() {
        if (Config.pmpBambooInBiomic && Loader.isModLoaded("plantmegapack")) {
            // Moso by default, Tropical Blue in the sinkhole's jungle, Giant Timber in the fossilized fenlands.
            EcologyOverlays.register("leaf-decor.json", ECOLOGY + "bamboo-leaf-decor.json");
            ForeignGenerators
                .standDown("growthcraft.bamboo.common.world.WorldGeneratorBamboo", ForeignGenerators.Scope.OVERWORLD);
        }
        if (Config.madakeInBiomic) EcologyOverlays.register("modern-features.json", ECOLOGY + "madake.json");
        if (Config.bambooShootsInBiomic && Config.enableBambooShoots)
            EcologyOverlays.register("palettes.json", ECOLOGY + "shoots.json");
    }

    /** At postInit, once the shoots exist. */
    static void postInit() {
        if (Config.madakeInBiomic) for (String[] names : MADAKE_NAMES) {
            LanguageRegistry.instance()
                .addStringLocalization("tile.biomic.imported_bamboo_leaves_leaves.name", names[0], names[1]);
            LanguageRegistry.instance()
                .addStringLocalization("tile.biomic.imported_bamboo_log_log.name", names[0], names[2]);
        }
        if (!Config.bambooShootsInBiomic || BambooShoots.item() == null) return;
        PlantLayer.shoots(id -> {
            BambooSpecies species = BambooSpecies.byId(id);
            BlockBambooShoot shoot = species == null ? null : BambooShoots.shoot(species);
            // Mostly young, so a finder has time to harvest them before they turn into bamboo.
            return shoot == null ? null : new PlantLayer.Patch() {

                @Override
                public Block block() {
                    return shoot;
                }

                @Override
                public int metadata(double roll) {
                    return (int) (roll * 6);
                }
            };
        });
        // HarvestCraft's one generic shoot crop, written while a chunk generates (Village Names' farms), becomes a Moso
        // shoot at the same stage. Players may still plant HarvestCraft's.
        Block pamShoot = Block.getBlockFromName("harvestcraft:pambambooshootCrop");
        Block moso = BambooShoots.shoot(BambooSpecies.MOSO);
        if (pamShoot != null && pamShoot != Blocks.air && moso != null)
            BlockSubstitutes.register(pamShoot, BlockSubstitutes.Scope.GENERATION, (chunk, localX, localZ) -> moso);
    }
}
