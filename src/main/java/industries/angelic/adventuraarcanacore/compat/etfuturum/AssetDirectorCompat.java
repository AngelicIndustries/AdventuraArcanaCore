package industries.angelic.adventuraarcanacore.compat.etfuturum;

import java.util.List;
import java.util.Map;

import makamys.mclib.ext.assetdirector.ADConfig;
import makamys.mclib.ext.assetdirector.AssetDirectorAPI;

/** Direct calls into Et Futurum's bundled AssetDirector, isolated so nothing links them without Et Futurum. */
public final class AssetDirectorCompat {

    private AssetDirectorCompat() {}

    public static void register(Map<String, List<String>> assets) {
        ADConfig config = new ADConfig();
        for (Map.Entry<String, List<String>> version : assets.entrySet()) {
            for (String path : version.getValue()) config.addObject(version.getKey(), path);
        }
        AssetDirectorAPI.register(config);
    }
}
