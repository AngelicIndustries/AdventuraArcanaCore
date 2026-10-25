package industries.angelic.adventuraarcanacore;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class Config {

    private static Configuration configuration;
    public static boolean debugMode;
    public static boolean enableCompartments = true;
    public static boolean enableWorktable = true;

    private Config() {}

    public static final class Categories {

        public static final String GENERAL = "general";

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
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static Configuration getRawConfig() {
        return configuration;
    }
}
