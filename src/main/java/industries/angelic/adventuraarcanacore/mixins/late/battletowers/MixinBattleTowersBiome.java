package industries.angelic.adventuraarcanacore.mixins.late.battletowers;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Battle Towers checks a new chunk's biome against its biome list before trying the chunk as a site. It passed the
 * chunk's coordinates where block coordinates belong, so it read the biome 16 times closer to the world's origin than
 * the chunk, and a biome switched off in its config lost its towers somewhere else. The biome is now read where the
 * tower would stand.
 */
@Pseudo
@Mixin(targets = "atomicstryker.battletowers.common.WorldGenHandler", remap = false)
public abstract class MixinBattleTowersBiome {

    @Redirect(
        method = "generate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBiomeGenForCoords(II)Lnet/minecraft/world/biome/BiomeGenBase;",
            remap = true),
        remap = false,
        require = 1)
    private BiomeGenBase adventuraarcanacore$siteBiome(World world, int chunkX, int chunkZ) {
        return world.getBiomeGenForCoords(chunkX << 4, chunkZ << 4);
    }
}
