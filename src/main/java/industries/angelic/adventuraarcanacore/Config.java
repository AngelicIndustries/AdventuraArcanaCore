package industries.angelic.adventuraarcanacore;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class Config {

    private static Configuration configuration;
    public static boolean debugMode;
    public static boolean enableCompartments = true;
    public static boolean enableWorktable = true;
    public static boolean pmpIvyForVines = true;

    private Config() {}

    public static final class Categories {

        public static final String GENERAL = "general";
        /** Read before preInit by core.PatchSwitches, which selects the mixins. */
        public static final String PATCHES = industries.angelic.adventuraarcanacore.core.PatchSwitches.CATEGORY;
        public static final String BIOMIC = "biomic";

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
            "saltyModDedicatedServer",
            "SaltyMod's client code stays off a dedicated server, which crashes on it otherwise.");
        configuration.setCategoryLanguageKey(Categories.BIOMIC, AdventuraArcanaCore.MODID + ".config.biomic");
        configuration.setCategoryComment(
            Categories.BIOMIC,
            "The pack's additions to Biomic's worlds, each applied only with Biomic and the mods it names. Changes need a restart.");
        configuration.setCategoryRequiresMcRestart(Categories.BIOMIC, true);
        pmpIvyForVines = biomic(
            "pmpIvyForVines",
            "Vanilla's vine becomes Plant Mega Pack's ivy in Biomic's worlds (Swedish where hot and wet, Japanese elsewhere). Needs Plant Mega Pack.");
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
