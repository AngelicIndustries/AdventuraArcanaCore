package industries.angelic.adventuraarcanacore.compat.angelica;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

import net.coderbot.iris.Iris;
import net.coderbot.iris.shaderpack.ProgramSet;
import net.coderbot.iris.shaderpack.ShaderPack;
import net.coderbot.iris.shaderpack.StringPair;

import cpw.mods.fml.common.Loader;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Shader packs give every modded dimension their overworld programs, compiled with the player's options. A dimension
 * can bring option values of its own for a pack (clouds and stars off over Dimensional Doors' Limbo, a darker
 * atmosphere): its programs then come from a second copy of the pack, compiled with those values on top of the
 * player's. The values live in config/adventuraarcanacore/shader_dimensions/DIMENSION/PACK.txt, in the format of
 * shaderpacks/PACK.txt, where PACK is the start of the shader pack's name (ComplementaryReimagined, BSL). Nothing of
 * the pack itself is changed.
 */
public final class DimensionShaderOptions {

    private static final String FOLDER = "adventuraarcanacore/shader_dimensions";

    /** What each pack was made from, to make its copies the same way. */
    private static final Map<ShaderPack, Made> MADE = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<ShaderPack, Map<String, Copy>> COPIES = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Set<ShaderPack> IS_COPY = Collections
        .newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));
    private static final ThreadLocal<Boolean> MAKING = ThreadLocal.withInitial(() -> false);

    private DimensionShaderOptions() {}

    public static void remember(ShaderPack pack, Path root, Map<String, String> options,
        Iterable<StringPair> environmentDefines) {
        if (MAKING.get()) return;
        List<StringPair> defines = new ArrayList<>();
        for (StringPair define : environmentDefines) defines.add(define);
        MADE.put(pack, new Made(root, new HashMap<>(options), defines));
    }

    /** The programs a dimension with its own option values uses, or null for the pack's usual ones. */
    public static ProgramSet programSet(ShaderPack pack, String dimensionName) {
        if (MAKING.get() || dimensionName == null || IS_COPY.contains(pack)) return null;
        Made made = MADE.get(pack);
        if (made == null) return null;
        Map<String, Copy> copies = COPIES.computeIfAbsent(pack, key -> new HashMap<>());
        Copy copy = copies.get(dimensionName);
        // A copy read from a zip that Angelica has since closed (it reopens the zip when it reloads) is made again
        if (copy != null && !copy.root.getFileSystem()
            .isOpen()) copy = null;
        if (copy == null) {
            // Not remembered when there are none: an early warm-up can ask before the pack's name is known
            Map<String, String> own = optionsFor(dimensionName, Iris.getCurrentPackName());
            if (own == null) return null;
            copy = makeCopy(made, own, dimensionName);
            if (copy == null) return null;
            copies.put(dimensionName, copy);
        }
        return copy.pack.getProgramSet(dimensionName);
    }

    private static Copy makeCopy(Made made, Map<String, String> own, String dimensionName) {
        Map<String, String> options = new HashMap<>(made.options);
        options.putAll(own);
        Path root = liveRoot(made.root);
        MAKING.set(true);
        try {
            Copy copy = new Copy(new ShaderPack(root, options, made.defines), root);
            IS_COPY.add(copy.pack);
            AdventuraArcanaCore.LOG.info("Shader options of their own for {}: {}", dimensionName, own);
            return copy;
        } catch (IOException | RuntimeException e) {
            AdventuraArcanaCore.LOG
                .warn("Couldn't compile {}'s own shader options; it uses the pack's", dimensionName, e);
            return null;
        } finally {
            MAKING.set(false);
        }
    }

    /** The dimension's file whose name the pack's name starts with (the longest such), or null. */
    public static File optionsFile(String dimensionName, String packName) {
        if (dimensionName == null || packName == null) return null;
        File[] files = new File(
            new File(
                Loader.instance()
                    .getConfigDir(),
                FOLDER),
            dimensionName).listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) return null;
        String pack = packName.toLowerCase(Locale.ROOT);
        File best = null;
        for (File file : files) {
            String prefix = file.getName()
                .substring(
                    0,
                    file.getName()
                        .length() - 4)
                .toLowerCase(Locale.ROOT);
            if (pack.startsWith(prefix) && (best == null || prefix.length() > best.getName()
                .length() - 4)) best = file;
        }
        return best;
    }

    private static Map<String, String> optionsFor(String dimensionName, String packName) {
        File best = optionsFile(dimensionName, packName);
        if (best == null) return null;
        Properties properties = new Properties();
        try (Reader reader = new FileReader(best)) {
            properties.load(reader);
        } catch (IOException e) {
            AdventuraArcanaCore.LOG.warn("Couldn't read {}", best, e);
            return null;
        }
        Map<String, String> options = new HashMap<>();
        for (String key : properties.stringPropertyNames()) options.put(
            key,
            properties.getProperty(key)
                .trim());
        return options;
    }

    /**
     * The pack's folder as it can be read now. Angelica reuses a pack across reloads but closes the zip it read it from
     * and opens it again; a folder in the closed zip is found again in the open one (Iris.zipFileSystem, read by
     * reflection: Iris loads before AAC's mixins could reach it).
     */
    private static Path liveRoot(Path root) {
        if (root.getFileSystem()
            .isOpen()) return root;
        try {
            Field field = Iris.class.getDeclaredField("zipFileSystem");
            field.setAccessible(true);
            FileSystem zip = (FileSystem) field.get(null);
            if (zip != null && zip.isOpen()) return zip.getPath(root.toString());
        } catch (ReflectiveOperationException | RuntimeException e) {
            AdventuraArcanaCore.LOG.debug("Couldn't find the shader pack's open zip", e);
        }
        return root;
    }

    private static final class Copy {

        final ShaderPack pack;
        final Path root;

        Copy(ShaderPack pack, Path root) {
            this.pack = pack;
            this.root = root;
        }
    }

    private static final class Made {

        final Path root;
        final Map<String, String> options;
        final List<StringPair> defines;

        Made(Path root, Map<String, String> options, List<StringPair> defines) {
            this.root = root;
            this.options = options;
            this.defines = defines;
        }
    }
}
