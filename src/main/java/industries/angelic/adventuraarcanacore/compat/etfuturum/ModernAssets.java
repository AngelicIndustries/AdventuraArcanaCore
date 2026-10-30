package industries.angelic.adventuraarcanacore.compat.etfuturum;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Registers the modern Minecraft assets listed in config/adventuraarcanacore/assetdirector.json ({"1.21.10":
 * ["minecraft/sounds/music/...ogg"]}) with Et Futurum's AssetDirector, which downloads them from Mojang and serves them
 * as the minecraft_1.21.10 resource domain. SoundWorkbench's music export writes the list. Stops the game when the
 * list can't be registered, instead of leaving those tracks silent. Links no Et Futurum class itself.
 */
public final class ModernAssets {

    public static final String FILE = AdventuraArcanaCore.MODID + "/assetdirector.json";
    private static final String COMPAT = "industries.angelic.adventuraarcanacore.compat.etfuturum.AssetDirectorCompat";

    private ModernAssets() {}

    /** Call during construction: AssetDirector fetches in pre-init. */
    public static void register(File configDir) {
        File file = new File(configDir, FILE);
        if (!FMLLaunchHandler.side()
            .isClient() || !file.isFile()) return;
        Map<String, List<String>> assets = read(file);
        if (assets == null || assets.isEmpty()) return;
        if (!Loader.isModLoaded("etfuturum")) {
            AdventuraArcanaCore.LOG
                .warn("{} lists modern assets, but Et Futurum Requiem isn't installed; skipping them", file);
            return;
        }
        try {
            Class.forName(COMPAT)
                .getMethod("register", Map.class)
                .invoke(null, assets);
        } catch (InvocationTargetException e) {
            throw fail(file, e.getCause());
        } catch (ReflectiveOperationException | LinkageError e) {
            throw fail(file, e);
        }
        int count = 0;
        for (List<String> paths : assets.values()) count += paths.size();
        AdventuraArcanaCore.LOG.info("Registered {} modern assets from {} with AssetDirector", count, file.getName());
    }

    private static Map<String, List<String>> read(File file) {
        Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
        try (Reader in = new FileReader(file)) {
            return new Gson().fromJson(in, type);
        } catch (IOException | JsonParseException e) {
            throw fail(file, e);
        }
    }

    private static IllegalStateException fail(File file, Throwable cause) {
        return new IllegalStateException(
            "Adventura Arcana Core: couldn't register the modern assets in " + file
                + ". Fix the file, or update AssetDirectorCompat for this Et Futurum version.",
            cause);
    }
}
