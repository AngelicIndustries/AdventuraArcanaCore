package industries.angelic.adventuraarcanacore.mixins.late.betterfoliage;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * One of a Better Foliage block filter's rules, a private class: whether it takes a block, and whether to keep it out.
 */
@Pseudo
@Mixin(targets = "mods.betterfoliage.config.BlockFilterSet$BlockFilterRule", remap = false)
public interface AccessorBlockFilterRule {

    @Invoker(value = "matchesBlock", remap = false)
    boolean adventuraarcanacore$matchesBlock(Block block, int metadata);

    @Accessor(value = "isBlockingRule", remap = false)
    boolean adventuraarcanacore$isBlockingRule();
}
