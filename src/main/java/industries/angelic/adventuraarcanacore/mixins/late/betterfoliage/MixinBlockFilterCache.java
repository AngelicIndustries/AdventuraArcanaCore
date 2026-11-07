package industries.angelic.adventuraarcanacore.mixins.late.betterfoliage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Better Foliage's block filters remember each block's answer in a plain HashMap, filled as chunks are drawn. Angelica
 * draws chunks on several threads, and a lookup during another thread's put could find the block and then get nothing
 * back, crashing the game (a NullPointerException in BlockFilterSet.matches). A concurrent map always gives back what
 * it says it has.
 */
@Pseudo
@Mixin(targets = "mods.betterfoliage.config.BlockFilterSet", remap = false)
public abstract class MixinBlockFilterCache {

    @Shadow(remap = false)
    @Final
    @Mutable
    private Map<Long, Boolean> blockCache;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"), remap = false, require = 1)
    private void adventuraarcanacore$concurrentCache(List<?> rules, CallbackInfo ci) {
        blockCache = new ConcurrentHashMap<>();
    }
}
