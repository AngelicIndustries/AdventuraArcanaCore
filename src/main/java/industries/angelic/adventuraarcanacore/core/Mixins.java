package industries.angelic.adventuraarcanacore.core;

import com.gtnewhorizon.gtnhmixins.builders.IBaseTransformer.Phase;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

/** Patch registrations. See docs/MIXINS.md for early, late, sided and conditional examples. */
public enum Mixins implements IMixins {

    GROWTHCRAFT_OPTIONAL_FORESTRY(
        new MixinBuilder("Make Growthcraft Forestry integration optional").setPhase(Phase.EARLY)
            .setApplyIf(() -> PatchSwitches.enabled("growthcraftWithoutForestry"))
            .addCommonMixins("fml.MixinFMLModContainer")),
    NATURA_NO_CLOUDS(new MixinBuilder("Generate no Natura clouds").setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.NATURA)
        .setApplyIf(() -> PatchSwitches.enabled("noNaturaClouds"))
        .addCommonMixins("natura.MixinNaturaNoClouds")),
    SALTYMOD_DEDICATED_SERVER(
        new MixinBuilder("Keep SaltyMod's client code off a dedicated server").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.SALTYMOD)
            .setApplyIf(() -> PatchSwitches.enabled("saltyModDedicatedServer"))
            .addServerMixins(
                "saltymod.MixinBeeNestServerIcons",
                "saltymod.MixinNeiServerRegistration",
                "saltymod.MixinServerRenderType")),
    WILD_CROPS_STAY(new MixinBuilder("Keep HarvestCraft's crops where they grow wild").setPhase(Phase.EARLY)
        .setApplyIf(() -> PatchSwitches.enabled("wildCropsStay"))
        .addCommonMixins("minecraft.MixinBlockBushWildCrops")),
    PMP_HARVESTCRAFT_BERRIES(
        new MixinBuilder("Give HarvestCraft's berries from Plant Mega Pack's bushes").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.PLANTMEGAPACK)
            .setApplyIf(() -> PatchSwitches.enabled("pmpHarvestCraftBerries"))
            .addCommonMixins("plantmegapack.MixinPmpBerryLinks")),
    SALTYMOD_BEE_NESTS(new MixinBuilder("Seed SaltyMod's bee nests per chunk").setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.SALTYMOD)
        .setApplyIf(() -> PatchSwitches.enabled("saltyModBeeNests"))
        .addCommonMixins("saltymod.MixinSaltyBeeNestSeed"));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
