package industries.angelic.adventuraarcanacore.mixins.late.saltymod;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.Config;

/**
 * SaltyMod places bee nests and burrows after population, from one clock-seeded random, so the same seed gave
 * different nests in every run: it is seeded per chunk from the world seed instead. In Biomic's overworld, where EFR's
 * nests are the only wild hives (beeNestsInBiomic), it places none.
 */
@Pseudo
@Mixin(targets = "darkbum.saltymod.event.PopulateChunkEventHandler", remap = false)
public abstract class MixinSaltyBeeNestSeed {

    @Shadow(remap = false)
    @Final
    private static Random rand;

    @Inject(method = "onChunkPopulatePost", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$seedPerChunk(PopulateChunkEvent.Post event, CallbackInfo info) {
        if (Config.beeNestsInBiomic && biomicOverworld(event.world)) {
            info.cancel();
            return;
        }
        rand.setSeed(
            mix64(
                event.world.getSeed() ^ event.chunkX * 0x9E3779B97F4A7C15L
                    ^ event.chunkZ * 0xC2B2AE3D27D4EB4FL
                    ^ 0x5A17B33L));
    }

    private static boolean biomicOverworld(World world) {
        return world != null && !world.isRemote
            && world.provider.dimensionId == 0
            && "biomic".equals(
                world.getWorldInfo()
                    .getTerrainType()
                    .getWorldTypeName());
    }

    /** Biomic's seed mixer (WorldgenContext.mix64), so the nests stay where they were. */
    private static long mix64(long value) {
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
