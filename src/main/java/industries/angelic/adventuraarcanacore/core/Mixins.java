package industries.angelic.adventuraarcanacore.core;

import com.gtnewhorizon.gtnhmixins.builders.IBaseTransformer.Phase;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

/** Patch registrations. See docs/MIXINS.md for early, late, sided and conditional examples. */
public enum Mixins implements IMixins {

    GROWTHCRAFT_OPTIONAL_FORESTRY(
        new MixinBuilder("Make Growthcraft Forestry integration optional").setPhase(Phase.EARLY)
            .addCommonMixins("fml.MixinFMLModContainer"));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
