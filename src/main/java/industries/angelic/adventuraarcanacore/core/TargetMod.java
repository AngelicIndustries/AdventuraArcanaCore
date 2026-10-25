package industries.angelic.adventuraarcanacore.core;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

/** Optional targets, identified by mod ID or coremod class without loading their classes. */
public enum TargetMod implements ITargetMod {

    /** Natura builds whose classes the patches are written against. */
    NATURA(new TargetModBuilder().setModId("Natura")
        .setTargetClass("mods.natura.Natura")
        .testModVersion("Natura", version -> "2.8.19".equals(version) || "2.8.24".equals(version))),
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
