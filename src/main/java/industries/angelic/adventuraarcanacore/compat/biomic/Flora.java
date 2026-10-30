package industries.angelic.adventuraarcanacore.compat.biomic;

import industries.angelic.adventuraarcanacore.Config;
import industries.angelic.biomic.api.EcologyOverlays;
import industries.angelic.biomic.api.ForeignGenerators;

/**
 * The pack's flora in Biomic's habitats (ecology proposal v3), as overlays of Biomic's ecology files: other mods'
 * plants
 * in the palettes (Plant Mega Pack's above all, BOP's, SaltyMod's, Natura's), the plant forms (epiphytes and tree
 * mushrooms on trunks, vines, immersed and floating plants), the flower patches that replace Origen's vanilla ones, the
 * sky islands' plants, and Origen's leaf decor in BOP's and PMP's plants. A plant whose mod is missing is skipped.
 */
final class Flora {

    private static final String ECOLOGY = "/assets/adventuraarcanacore/biomic/ecology/";

    private Flora() {}

    /** At preInit: the overlays, and PMP's own layer stands down (the palettes place its plants by habitat). */
    static void preInit() {
        if (Config.floraInBiomic) {
            EcologyOverlays.register("palettes.json", ECOLOGY + "flora.json");
            ForeignGenerators.standDown("plantmegapack.worldgen.PMPWorldGenerator", ForeignGenerators.Scope.OVERWORLD);
        }
        if (Config.leafDecorInBiomic) EcologyOverlays.register("leaf-decor.json", ECOLOGY + "flora-leaf-decor.json");
        if (Config.decorInBiomic) EcologyOverlays.register("decor.json", ECOLOGY + "decor.json");
    }
}
