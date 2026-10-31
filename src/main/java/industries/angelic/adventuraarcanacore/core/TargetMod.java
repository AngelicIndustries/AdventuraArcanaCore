package industries.angelic.adventuraarcanacore.core;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

/** Optional targets, identified by mod ID or coremod class without loading their classes. */
public enum TargetMod implements ITargetMod {

    AETHER(new TargetModBuilder().setModId("aether_legacy")
        .setTargetClass("com.gildedgames.the_aether.Aether")),
    ANGELICA(new TargetModBuilder().setModId("angelica")),
    /** The Better Sleeping build the patches are written against. */
    BETTER_SLEEPING(new TargetModBuilder().setModId("bettersleeping")
        .setTargetClass("cz.ondraster.bettersleeping.BetterSleeping")
        .testModVersion("bettersleeping", "1.7.10-1.2.27")),
    BIOMES_O_PLENTY(new TargetModBuilder().setModId("BiomesOPlenty")),
    DISTANT_HORIZONS(new TargetModBuilder().setModId("distanthorizons")),
    DYNAMIC_SURROUNDINGS(new TargetModBuilder().setModId("dsurround")),
    ENDLESSIDS(new TargetModBuilder().setModId("endlessids")),
    /** Natura builds whose classes the patches are written against. */
    NATURA(new TargetModBuilder().setModId("Natura")
        .setTargetClass("mods.natura.Natura")
        .testModVersion("Natura", version -> "2.8.19".equals(version) || "2.8.24".equals(version))),
    OPENBLOCKS(new TargetModBuilder().setModId("OpenBlocks")
        .setTargetClass("openblocks.OpenBlocks")),
    PLANTMEGAPACK(new TargetModBuilder().setModId("plantmegapack")),
    RECURRENT_COMPLEX(new TargetModBuilder().setModId("reccomplex")
        .setTargetClass("ivorius.reccomplex.RecurrentComplex")),
    RUINS(new TargetModBuilder().setModId("AS_Ruins")
        .setTargetClass("atomicstryker.ruins.common.RuinsMod")),
    /** The SaltyMod build the patches are written against (its runtime version string). */
    SALTYMOD(new TargetModBuilder().setModId("saltymod")
        .setTargetClass("darkbum.saltymod.SaltyMod")
        .testModVersion("saltymod", "2.0.0-master.150+9a0954aa6f"));

    private final TargetModBuilder builder;

    TargetMod(TargetModBuilder builder) {
        this.builder = builder;
    }

    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
