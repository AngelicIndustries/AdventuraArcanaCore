package industries.angelic.adventuraarcanacore.mixins.late.angelica;

import net.coderbot.iris.block_rendering.BlockRenderingSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.Config;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;

/**
 * Shader packs only list vanilla blocks in their block.properties, so a modded plant neither waves nor gets a plant's
 * shading. Blocks in shaderBlockAliases take the IDs of the vanilla block they're aliased to (the Fadebloom a
 * flower's).
 */
@Mixin(value = BlockRenderingSettings.class, remap = false)
public abstract class MixinShaderBlockAliases {

    @Inject(method = "setBlockMetaMatches", at = @At("HEAD"), require = 1)
    private void adventuraarcanacore$alias(Reference2ObjectMap<Block, Int2IntMap> blockMetaIds, CallbackInfo ci) {
        if (blockMetaIds == null) return;
        for (String entry : Config.shaderBlockAliases) {
            int split = entry.indexOf('=');
            if (split <= 0) continue;
            Block block = Block.getBlockFromName(
                entry.substring(0, split)
                    .trim());
            Block like = Block.getBlockFromName(
                entry.substring(split + 1)
                    .trim());
            if (block == null || block == Blocks.air || like == null || like == Blocks.air) continue;
            Int2IntMap ids = blockMetaIds.get(like);
            if (ids != null && !blockMetaIds.containsKey(block)) blockMetaIds.put(block, ids);
            else if (ids == null)
                AdventuraArcanaCore.LOG.debug("Shader pack has no IDs for {}, {} keeps none", like, block);
        }
    }
}
