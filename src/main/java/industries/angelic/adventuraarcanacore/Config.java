package industries.angelic.adventuraarcanacore;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class Config {

    private static Configuration configuration;
    public static boolean debugMode;
    public static boolean enableCompartments = true;
    public static boolean enableWorktable = true;
    public static boolean pmpIvyForVines = true;
    public static boolean enableWildFood = true;
    public static boolean foodLinks = true;
    public static boolean wildFoodInBiomic = true;
    public static boolean beeNestsInBiomic = true;
    public static boolean enableBambooShoots = true;
    public static boolean growthcraftGrapes = true;
    public static boolean pmpBambooInBiomic = true;
    public static boolean madakeInBiomic = true;
    public static boolean bambooShootsInBiomic = true;
    public static boolean floraInBiomic = true;
    public static boolean leafDecorInBiomic = true;
    public static boolean netherFoodInBiomic = true;
    public static boolean decorInBiomic = true;
    public static boolean caveMoods;
    public static boolean wolfHowls = true;

    private Config() {}

    public static final class Categories {

        public static final String GENERAL = "general";
        /** Read before preInit by core.PatchSwitches, which selects the mixins. */
        public static final String PATCHES = industries.angelic.adventuraarcanacore.core.PatchSwitches.CATEGORY;
        public static final String BIOMIC = "biomic";
        public static final String SOUNDS = "sounds";

        private Categories() {}
    }

    public static void loadConfig(File file) {
        configuration = new Configuration(file);
        configuration.load();
        sync();
    }

    public static void sync() {
        configuration.setCategoryLanguageKey(Categories.GENERAL, AdventuraArcanaCore.MODID + ".config.general");
        debugMode = configuration.getBoolean(
            "debugMode",
            Categories.GENERAL,
            false,
            "Enable debug logging.",
            AdventuraArcanaCore.MODID + ".config.debugMode");
        enableCompartments = configuration
            .get(
                Categories.GENERAL,
                "enableCompartments",
                true,
                "Enable compartment blocks and recipes. Requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableCompartments")
            .setRequiresMcRestart(true)
            .getBoolean();
        enableWorktable = configuration
            .get(
                Categories.GENERAL,
                "enableWorktable",
                true,
                "Enable the worktable and its recipe. Requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableWorktable")
            .setRequiresMcRestart(true)
            .getBoolean();
        enableWildFood = configuration.get(
            Categories.GENERAL,
            "enableWildFood",
            true,
            "Wild berry bushes, fruit pods and fruiting vines for HarvestCraft's crops that have no natural form of their own. Needs HarvestCraft; requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableWildFood")
            .setRequiresMcRestart(true)
            .getBoolean();
        foodLinks = configuration.get(
            Categories.GENERAL,
            "foodLinks",
            true,
            "Other mods' wild plants drop HarvestCraft food: BOP's rice, sunflowers' seeds, PMP's berries and lettuce. Needs HarvestCraft; requires a restart.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.foodLinks")
            .setRequiresMcRestart(true)
            .getBoolean();
        enableBambooShoots = configuration.get(
            Categories.GENERAL,
            "enableBambooShoots",
            true,
            "A shoot for each bamboo species, in place of HarvestCraft's one shoot crop: picked when grown, it turns into its bamboo if left. Needs HarvestCraft; the bamboos need Plant Mega Pack, Madake needs Biomic. Requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableBambooShoots")
            .setRequiresMcRestart(true)
            .getBoolean();
        growthcraftGrapes = configuration.get(
            Categories.GENERAL,
            "growthcraftGrapes",
            true,
            "Growthcraft's grape is the only grape: the wild grape vines give Growthcraft's grapes and grape seeds, and NEI hides HarvestCraft's grape, grape seeds and grape crop (HarvestCraft's recipes take Growthcraft's grapes). Needs Growthcraft Grapes; requires a restart.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.growthcraftGrapes")
            .setRequiresMcRestart(true)
            .getBoolean();
        configuration.setCategoryLanguageKey(Categories.PATCHES, AdventuraArcanaCore.MODID + ".config.patches");
        configuration.setCategoryComment(
            Categories.PATCHES,
            "Patches to other mods, each applied only with its mod. Changes need a restart.");
        configuration.setCategoryRequiresMcRestart(Categories.PATCHES, true);
        patch(
            "growthcraftWithoutForestry",
            "Growthcraft loads without Forestry: its Forestry integration becomes optional.");
        patch("noNaturaClouds", "Natura generates no clouds in any dimension.");
        patch(
            "wildCropsStay",
            "HarvestCraft's and Harvest the Nether's crops stay where they grow wild (grass, sand, netherrack); sowing still needs farmland.");
        patch("pmpHarvestCraftBerries", "Plant Mega Pack's berry bushes give HarvestCraft's berries (with foodLinks).");
        patch(
            "naturaBerriesStandDown",
            "Natura's wild berry bushes are not generated in Biomic's overworld while wildFoodInBiomic is on.");
        patch(
            "saltyModBeeNests",
            "SaltyMod's bee nests are seeded per chunk, so a seed gives the same nests; none in Biomic's overworld while beeNestsInBiomic is on.");
        patch(
            "saltyModDedicatedServer",
            "SaltyMod's client code stays off a dedicated server, which crashes on it otherwise.");
        patch(
            "angelicaDsWeather",
            "Dynamic Surroundings' rain sounds, splashes and auroras work with Angelica's weather, which otherwise skips them. The game stops at startup if Angelica or Dynamic Surroundings changed in a way the patch doesn't fit.");
        patch(
            "ruinsEndlessIdsStats",
            "Ruins sizes its biome statistics for EndlessIDs' biome IDs, which otherwise overflow them.");
        patch(
            "betterSleepingFixes",
            "Better Sleeping ends the night as soon as its share of players is asleep, not only when someone gets into bed; its sleeping message shows the real share, lying in bed makes nobody tired and the sleep bar's marker follows guiOffsetLeft.");
        patch(
            "hangGliderNerf",
            "OpenBlocks' hang glider glides about 2.5 blocks per block of height instead of 6, dives at a third of the speed, wears out after 4 minutes in the air and is repaired with leather.");
        configuration.setCategoryLanguageKey(Categories.BIOMIC, AdventuraArcanaCore.MODID + ".config.biomic");
        configuration.setCategoryComment(
            Categories.BIOMIC,
            "The pack's additions to Biomic's worlds, each applied only with Biomic and the mods it names. Changes need a restart.");
        configuration.setCategoryRequiresMcRestart(Categories.BIOMIC, true);
        pmpIvyForVines = biomic(
            "pmpIvyForVines",
            "Vanilla's vine becomes Plant Mega Pack's ivy in Biomic's worlds (Swedish where hot and wet, Japanese elsewhere). Needs Plant Mega Pack.");
        wildFoodInBiomic = biomic(
            "wildFoodInBiomic",
            "HarvestCraft's food grows wild in Biomic's habitats (crop patches, berry bushes, fruit groves, fruiting vines), and HarvestCraft's own gardens, fruit trees, hives and salt stand down there. Needs HarvestCraft.");
        beeNestsInBiomic = biomic(
            "beeNestsInBiomic",
            "Et Futurum's bee nests hang on trees in Biomic's habitats, the only wild hives there. Needs Et Futurum Requiem.");
        pmpBambooInBiomic = biomic(
            "pmpBambooInBiomic",
            "Origen's bamboo grows as Plant Mega Pack's species in Biomic's worlds (Moso by default, Tropical Blue in the sinkhole's jungle, Giant Timber in the fossilized fenlands), and Growthcraft's bamboo stands down. Needs Plant Mega Pack.");
        madakeInBiomic = biomic(
            "madakeInBiomic",
            "Regions Unexplored's bamboo tree is Madake in Biomic's worlds and grows in both of the Bamboo Forest's tree groups.");
        bambooShootsInBiomic = biomic(
            "bambooShootsInBiomic",
            "Bamboo shoots grow wild where their bamboo grows in Biomic's habitats, and HarvestCraft's generic shoot crop becomes a Moso shoot when a chunk generates. Needs HarvestCraft (enableBambooShoots).");
        floraInBiomic = biomic(
            "floraInBiomic",
            "Other mods' plants in Biomic's habitats (Plant Mega Pack's above all, BOP's, SaltyMod's, Natura's), epiphytes and tree mushrooms on trunks, vines, water plants, flower patches and the sky islands' plants; Plant Mega Pack's own generator stands down. Each plant needs its mod.");
        leafDecorInBiomic = biomic(
            "leafDecorInBiomic",
            "Origen's leaf decor in BOP's and Plant Mega Pack's plants: their shrubs in bush clumps, ivy and flowering vines on cliffs, moss and willow on walls. Each plant needs its mod.");
        netherFoodInBiomic = biomic(
            "netherFoodInBiomic",
            "Harvest the Nether grows wild in Biomic's Tartarus: its crops in patches by biome, glow flowers over Soul Valley, ignis trees here and there; its own gardens and trees stand down. Needs Harvest the Nether.");
        decorInBiomic = biomic(
            "decorInBiomic",
            "BOP's and Plant Mega Pack's blocks in Biomic's cave and sea decoration: moss, ivy, glowshrooms, stone formations, bones and miner's delight in caves; Plant Mega Pack's kelp, eelgrass, sea plants and corals in the seas, in place of Biomic's ported seagrass and algae. Each block needs its mod.");
        configuration.setCategoryLanguageKey(Categories.SOUNDS, AdventuraArcanaCore.MODID + ".config.sounds");
        configuration.setCategoryComment(Categories.SOUNDS, "The pack's sound changes.");
        caveMoods = configuration.get(
            Categories.SOUNDS,
            "caveMoods",
            false,
            "Vanilla's random cave sounds play underground, with the ones Et Futurum adds. Off so scary sounds come from real creatures; other mods' replacements, such as Netherlicious's Nether moods, still play. Client side.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.caveMoods")
            .getBoolean();
        wolfHowls = configuration
            .get(
                Categories.SOUNDS,
                "wolfHowls",
                true,
                "Wild wolves howl at night in the Overworld, and wild wolves nearby may join in. Server side.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.wolfHowls")
            .getBoolean();
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static boolean biomic(String key, String comment) {
        return configuration.get(Categories.BIOMIC, key, true, comment)
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config." + key)
            .setRequiresMcRestart(true)
            .getBoolean();
    }

    private static void patch(String key, String comment) {
        configuration.get(Categories.PATCHES, key, true, comment)
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config." + key)
            .setRequiresMcRestart(true);
    }

    public static Configuration getRawConfig() {
        return configuration;
    }
}
