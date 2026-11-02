package industries.angelic.adventuraarcanacore.core;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

/** Optional targets, identified by mod ID or coremod class without loading their classes. */
public enum TargetMod implements ITargetMod {

    ADVENTURE_BACKPACK(new TargetModBuilder().setModId("adventurebackpack")
        .setTargetClass("com.darkona.adventurebackpack.AdventureBackpack")),
    AETHER(new TargetModBuilder().setModId("aether_legacy")
        .setTargetClass("com.gildedgames.the_aether.Aether")),
    ANGELICA(new TargetModBuilder().setModId("angelica")),
    BATTLE_TOWERS(new TargetModBuilder().setModId("AS_BattleTowers")
        .setTargetClass("atomicstryker.battletowers.common.WorldGenHandler")),
    /** The Better Sleeping build the patches are written against. */
    BETTER_SLEEPING(new TargetModBuilder().setModId("bettersleeping")
        .setTargetClass("cz.ondraster.bettersleeping.BetterSleeping")
        .testModVersion("bettersleeping", "1.7.10-1.2.27")),
    BIBLIOCRAFT(new TargetModBuilder().setModId("BiblioCraft")
        .setTargetClass("jds.bibliocraft.BiblioCraft")),
    BIOMES_O_PLENTY(new TargetModBuilder().setModId("BiomesOPlenty")),
    DIMENSIONAL_DOORS(new TargetModBuilder().setModId("dimdoors")
        .setTargetClass("org.dimdev.dimdoors.core.DDTeleporter")),
    DISTANT_HORIZONS(new TargetModBuilder().setModId("distanthorizons")),
    DYNAMIC_SURROUNDINGS(new TargetModBuilder().setModId("dsurround")),
    ENDLESSIDS(new TargetModBuilder().setModId("endlessids")),
    ET_FUTURUM(new TargetModBuilder().setModId("etfuturum")),
    /** Hodgepodge builds with the chunk generation guard (preventEntityChunkLoading). */
    HODGEPODGE(new TargetModBuilder().setModId("hodgepodge")
        .setTargetClass("com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler")),
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
    VILLAGE_NAMES(new TargetModBuilder().setModId("VillageNames")
        .setTargetClass("astrotibs.villagenames.village.StructureVillageVN")),
    WILD_MOBS(new TargetModBuilder().setModId("wildmobsmod")
        .setTargetClass("com.wildmobsmod.main.WildMobsMod")),
    STRUCTURE_MEMORY(new TargetModBuilder().setModId("structurememory")
        .setTargetClass("xsoras.structurememory.memory.MemoryStore")),
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
