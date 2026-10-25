package industries.angelic.adventuraarcanacore.core;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

/** Optional targets, identified by mod ID or coremod class without loading their classes. */
public enum TargetMod implements ITargetMod {

    ;

    private final TargetModBuilder builder;

    TargetMod(TargetModBuilder builder) {
        this.builder = builder;
    }

    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
