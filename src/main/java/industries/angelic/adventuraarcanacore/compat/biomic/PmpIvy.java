package industries.angelic.adventuraarcanacore.compat.biomic;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

import industries.angelic.biomic.api.BlockSubstitutes;

import cpw.mods.fml.common.Loader;

/**
 * In Biomic's worlds vanilla's vine is not generated: every vine, whoever writes it, becomes a Plant Mega Pack ivy with
 * the same faces, Swedish ivy where it is hot and wet, Japanese ivy elsewhere.
 */
final class PmpIvy {

    private PmpIvy() {}

    static void register() {
        if (!Loader.isModLoaded("plantmegapack")) return;
        Block japanese = find("plantmegapack:vineJapaneseIvy"), swedish = find("plantmegapack:vineSwedishIvy");
        if (japanese == null) return;
        BlockSubstitutes.register(Blocks.vine, BlockSubstitutes.Scope.ALWAYS, (chunk, localX, localZ) -> {
            BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(localX, localZ, chunk.worldObj.getWorldChunkManager());
            boolean humid = biome != null && biome.temperature >= 0.9F && biome.rainfall >= 0.8F;
            return humid && swedish != null ? swedish : japanese;
        });
    }

    private static Block find(String id) {
        Block block = Block.getBlockFromName(id);
        return block == null || block == Blocks.air ? null : block;
    }
}
