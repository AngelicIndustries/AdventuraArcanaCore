package industries.angelic.adventuraarcanacore.compat.biomic;

import industries.angelic.adventuraarcanacore.Config;

/**
 * What the pack adds to Biomic's worlds through Biomic's API (org.fentanylsolutions.biomic.api). Only called when
 * Biomic is loaded; each feature has its switch in the "biomic" config category.
 */
public final class BiomicIntegration {

    private BiomicIntegration() {}

    /** At preInit: data and stand-downs, before Biomic reads them. */
    public static void preInit() {
        if (Config.wildFoodInBiomic) FoodPlants.preInit();
        Bamboo.preInit();
        Flora.preInit();
    }

    /** At postInit, once every mod's blocks exist. */
    public static void postInit() {
        if (Config.pmpIvyForVines) PmpIvy.register();
        if (Config.wildFoodInBiomic) FoodPlants.postInit();
        if (Config.beeNestsInBiomic) FoodPlants.nests();
        Bamboo.postInit();
    }
}
