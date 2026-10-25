package industries.angelic.adventuraarcanacore.food;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * The bamboos that grow from the shoots: PMP's nine species and Madake (Phyllostachys bambusoides, Regions Unexplored's
 * bamboo tree, which Biomic has). The order is the shoot item's metadata: append only.
 */
public enum BambooSpecies {

    ASPER("asper", "bambooAsper"),
    FARGESIA_ROBUSTA("fargesia_robusta", "bambooFargesiaRobusta"),
    GIANT_TIMBER("giant_timber", "bambooGiantTimber"),
    GOLDEN("golden", "bambooGolden"),
    MOSO("moso", "bambooMoso"),
    SHORT_TASSLED("short_tassled", "bambooShortTassled"),
    TIMOR_BLACK("timor_black", "bambooTimorBlack"),
    TROPICAL_BLUE("tropical_blue", "bambooTropicalBlue"),
    WET_FOREST("wet_forest", "bambooWetForest"),
    MADAKE("madake", null);

    /** The id in palettes and registry names. */
    public final String id;
    /** PMP's block name, or null for Madake, which grows as a tree. */
    final String pmp;

    BambooSpecies(String id, String pmp) {
        this.id = id;
        this.pmp = pmp;
    }

    /** PMP's bamboo block, or null (Madake, or PMP not installed). */
    Block bamboo() {
        if (pmp == null) return null;
        Block block = Block.getBlockFromName("plantmegapack:" + pmp);
        return block == null || block == Blocks.air ? null : block;
    }

    /** The domain and path of a texture of this bamboo's culm, whose colour its shoot takes. */
    String[] culmTexture() {
        return pmp == null ? new String[] { "biomic", "textures/blocks/ru/bamboo_log.png" }
            : new String[] { "plantmegapack", "textures/blocks/" + pmp + "0.png" };
    }

    public static BambooSpecies byId(String id) {
        for (BambooSpecies species : values()) if (species.id.equals(id)) return species;
        return null;
    }
}
