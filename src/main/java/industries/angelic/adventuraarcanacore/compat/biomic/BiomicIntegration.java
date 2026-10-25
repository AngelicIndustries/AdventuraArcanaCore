package industries.angelic.adventuraarcanacore.compat.biomic;

import industries.angelic.adventuraarcanacore.Config;

/**
 * What the pack adds to Biomic's worlds through Biomic's API (org.fentanylsolutions.biomic.api). Only called when
 * Biomic is loaded; each feature has its switch in the "biomic" config category.
 */
public final class BiomicIntegration {

    private BiomicIntegration() {}

    /** At postInit, once every mod's blocks exist. */
    public static void postInit() {
        if (Config.pmpIvyForVines) PmpIvy.register();
    }
}
