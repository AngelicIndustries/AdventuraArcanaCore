package industries.angelic.adventuraarcanacore;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class Config {

    private static Configuration configuration;
    public static boolean debugMode;

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
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static Configuration getRawConfig() {
        return configuration;
    }
}
