package industries.angelic.adventuraarcanacore.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.launchwrapper.Launch;

/**
 * The patch switches of {@code config/adventuraarcanacore.cfg} ("patches" category), read when the mixins are chosen,
 * before Forge's preInit loads {@code Config}. Read only, from the file as {@code Config} writes it; a missing file or
 * key means on. Loads no Minecraft or mod classes.
 */
public final class PatchSwitches {

    public static final String CATEGORY = "patches";
    private static Map<String, Boolean> switches;

    private PatchSwitches() {}

    public static synchronized boolean enabled(String key) {
        if (switches == null)
            switches = read(new File(new File(Launch.minecraftHome, "config"), "adventuraarcanacore.cfg"));
        Boolean value = switches.get(key);
        return value == null || value;
    }

    static Map<String, Boolean> read(File file) {
        Map<String, Boolean> values = new HashMap<>();
        if (!file.isFile()) return values;
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException unreadable) {
            return values;
        }
        return parse(lines);
    }

    /** The {@code B:key=value} entries directly inside the {@code patches} category. */
    static Map<String, Boolean> parse(List<String> lines) {
        Map<String, Boolean> values = new HashMap<>();
        int depth = 0;
        boolean inside = false;
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.endsWith("{")) {
                depth++;
                if (depth == 1 && line.substring(0, line.length() - 1)
                    .trim()
                    .replace("\"", "")
                    .equals(CATEGORY)) inside = true;
                continue;
            }
            if (line.equals("}")) {
                if (depth == 1) inside = false;
                depth = Math.max(0, depth - 1);
                continue;
            }
            if (!inside || depth != 1 || !line.startsWith("B:")) continue;
            int equals = line.indexOf('=');
            if (equals < 0) continue;
            String key = line.substring(2, equals)
                .trim()
                .replace("\"", "");
            values.put(
                key,
                Boolean.parseBoolean(
                    line.substring(equals + 1)
                        .trim()));
        }
        return values;
    }
}
