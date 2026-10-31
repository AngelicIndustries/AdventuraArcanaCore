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
        .addCommonMixins("saltymod.MixinSaltyBeeNestSeed")),
    NATURA_BERRIES_STAND_DOWN(
        new MixinBuilder("Keep Natura's wild berries out of Biomic's overworld").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.NATURA)
            .setApplyIf(() -> PatchSwitches.enabled("naturaBerriesStandDown"))
            .addCommonMixins("natura.MixinNaturaBerryOwnership")),
    ANGELICA_DS_WEATHER(
        new MixinBuilder("Run Dynamic Surroundings' rain sounds, splashes and auroras beside Angelica's weather")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.ANGELICA)
            .addRequiredMod(TargetMod.DYNAMIC_SURROUNDINGS)
            .setApplyIf(() -> PatchSwitches.enabled("angelicaDsWeather"))
            .addClientMixins("angelica.MixinWeatherRenderer")),
    RUINS_ENDLESSIDS_STATS(new MixinBuilder("Size Ruins' biome statistics for EndlessIDs").setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.RUINS)
        .addRequiredMod(TargetMod.ENDLESSIDS)
        .setApplyIf(() -> PatchSwitches.enabled("ruinsEndlessIdsStats"))
        .addCommonMixins("ruins.MixinRuinStats")),
    RUINS_WORLD_HANDLES(new MixinBuilder("Give each world its own Ruins records").setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.RUINS)
        .setApplyIf(() -> PatchSwitches.enabled("ruinsWorldHandles"))
        .addCommonMixins("ruins.MixinRuinsWorldHandles")),
    DISTANT_HORIZONS_CHUNK_TASKS(
        new MixinBuilder("Generate Distant Horizons' chunks one server task each").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.DISTANT_HORIZONS)
            .setApplyIf(() -> PatchSwitches.enabled("distantHorizonsChunkTasks"))
            .addCommonMixins("distanthorizons.MixinDhInternalServerGenerator")),
    DISTANT_HORIZONS_QUICK_SHUTDOWN(
        new MixinBuilder("Cancel Distant Horizons' server tasks before its thread pools shut down").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.DISTANT_HORIZONS)
            .setApplyIf(() -> PatchSwitches.enabled("distantHorizonsQuickShutdown"))
            .addCommonMixins("distanthorizons.MixinThreadPoolUtil")),
    RECURRENT_COMPLEX_NATURAL_FILL(
        new MixinBuilder("Dedupe the columns of Recurrent Complex's natural fill").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.RECURRENT_COMPLEX)
            .setApplyIf(() -> PatchSwitches.enabled("reccomplexNaturalFill"))
            .addCommonMixins("reccomplex.MixinTransformerNatural")),
    BETTER_SLEEPING_FIXES(new MixinBuilder("Fix Better Sleeping's sleep percentage, sleep message, bed and sleep bar")
        .setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.BETTER_SLEEPING)
        .setApplyIf(() -> PatchSwitches.enabled("betterSleepingFixes"))
        .addCommonMixins("bettersleeping.MixinBetterSleepingEvents", "bettersleeping.MixinAlternateSleep")
        .addClientMixins("bettersleeping.MixinSleepOverlay")),
    HANG_GLIDER_NERF(new MixinBuilder("Make OpenBlocks' hang glider glide worse and wear out").setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.OPENBLOCKS)
        .setApplyIf(() -> PatchSwitches.enabled("hangGliderNerf"))
        .addCommonMixins("openblocks.MixinEntityHangGlider", "openblocks.MixinItemHangGlider")),
    AETHER_PARACHUTE_RIDER(
        new MixinBuilder("Drop the Aether's parachutes when their rider changes worlds").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.AETHER)
            .setApplyIf(() -> PatchSwitches.enabled("aetherParachuteRider"))
            .addCommonMixins("aether.MixinParachuteRider")),
    AETHER_GLOVE_ACCESSORIES(
        new MixinBuilder("Register the Aether's copper and netherite gloves under their own options")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.AETHER)
            .setApplyIf(() -> PatchSwitches.enabled("aetherGloveAccessories"))
            .addCommonMixins("aether.MixinGloveAccessories")),
    AETHER_WING_FLAP(new MixinBuilder("Flap each player's Aether wings with their own beat").setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.AETHER)
        .setApplyIf(() -> PatchSwitches.enabled("aetherWingFlap"))
        .addCommonMixins("aether.MixinWingBeat")
        .addClientMixins("aether.AccessorPlayerAether", "aether.MixinWingFlap")),
    BOP_NO_WORLD_TYPE_SCREEN(new MixinBuilder("Skip Biomes O' Plenty's world type screen").setPhase(Phase.LATE)
        .addRequiredMod(TargetMod.BIOMES_O_PLENTY)
        .setApplyIf(() -> PatchSwitches.enabled("bopNoWorldTypeScreen"))
        .addClientMixins("biomesoplenty.MixinWorldTypeScreen")),
    DISTANT_HORIZONS_NO_DEV_BUILD_MESSAGE(
        new MixinBuilder("Skip Distant Horizons' unstable build message").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.DISTANT_HORIZONS)
            .setApplyIf(() -> PatchSwitches.enabled("distantHorizonsNoDevBuildMessage"))
            .addClientMixins("distanthorizons.MixinClientApiDevBuild")),
    ET_FUTURUM_NO_EXPERIMENTAL_MESSAGE(
        new MixinBuilder("Skip Et Futurum's experimental features message").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.ET_FUTURUM)
            .setApplyIf(() -> PatchSwitches.enabled("etFuturumNoExperimentalMessage"))
            .addClientMixins("etfuturum.MixinExperimentalWarning")),
    DIMDOORS_ENTITY_CHUNK_GEN(
        new MixinBuilder("Let mobs going through Dimensional Doors generate the pocket they enter").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.DIMENSIONAL_DOORS)
            .addRequiredMod(TargetMod.HODGEPODGE)
            .setApplyIf(() -> PatchSwitches.enabled("dimDoorsEntityChunkGen"))
            .addCommonMixins("dimdoors.MixinDDTeleporterChunkGen")),
    STRUCTURE_MEMORY_PER_SAVE(
        new MixinBuilder("Give each save and server its own Structure Memory").setPhase(Phase.LATE)
            .addRequiredMod(TargetMod.STRUCTURE_MEMORY)
            .setApplyIf(() -> PatchSwitches.enabled("structureMemoryPerSave"))
            .addClientMixins("structurememory.MixinMemoryStoreWorldKey"));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
