package industries.angelic.adventuraarcanacore.mixins.late.betterfoliage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Better Foliage's block filters remember each block's answer in a plain HashMap keyed by a boxed long, filled as
 * chunks are drawn. Angelica draws chunks on several threads, and a lookup during another thread's put could find the
 * block and then get nothing back, crashing the game (a NullPointerException in BlockFilterSet.matches). Every lookup
 * also boxed a new key: half of the garbage chunk drawing made. The answers now sit in a bit mask per block ID, read
 * with no lock and no allocation; metadata above 15, which are rare, go to a concurrent map.
 */
@Pseudo
@Mixin(targets = "mods.betterfoliage.config.BlockFilterSet", remap = false)
public abstract class MixinBlockFilterCache {

    @Shadow(remap = false)
    @Final
    private List<?> rules;

    /** By block ID, for metadata 0 to 15: the low 16 bits mark those worked out, the high 16 those that match. */
    @Unique
    private volatile AtomicIntegerArray adventuraarcanacore$byBlockId;
    @Unique
    private Map<Long, Boolean> adventuraarcanacore$otherMetadata;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"), remap = false, require = 1)
    private void adventuraarcanacore$caches(List<?> rules, CallbackInfo ci) {
        adventuraarcanacore$byBlockId = new AtomicIntegerArray(4096);
        adventuraarcanacore$otherMetadata = new ConcurrentHashMap<>();
    }

    /**
     * @author jack
     * @reason It runs for every block of every chunk drawn, on several threads at once, so it must neither lock nor
     *         allocate, and a cancellable inject would allocate on every call.
     */
    @Overwrite(remap = false)
    public boolean matches(Block block, int metadata) {
        int blockID = Block.blockRegistry.getIDForObject(block);
        if (blockID < 0 || metadata < 0 || metadata > 15) {
            long key = ((long) blockID << Integer.SIZE) | (metadata & 0xFFFFFFFFL);
            return adventuraarcanacore$otherMetadata
                .computeIfAbsent(key, k -> adventuraarcanacore$evaluate(block, metadata));
        }
        AtomicIntegerArray cache = adventuraarcanacore$cacheFor(blockID);
        int known = 1 << metadata;
        int entry = cache.get(blockID);
        if ((entry & known) != 0) return (entry & (known << 16)) != 0;

        boolean matches = adventuraarcanacore$evaluate(block, metadata);
        int add = known | (matches ? known << 16 : 0);
        int previous;
        do {
            previous = cache.get(blockID);
        } while (!cache.compareAndSet(blockID, previous, previous | add));
        return matches;
    }

    /** The cache, grown to hold this block ID. An answer stored in the old one while it grows is worked out again. */
    @Unique
    private AtomicIntegerArray adventuraarcanacore$cacheFor(int blockID) {
        AtomicIntegerArray cache = adventuraarcanacore$byBlockId;
        if (blockID < cache.length()) return cache;
        synchronized (this) {
            cache = adventuraarcanacore$byBlockId;
            if (blockID >= cache.length()) {
                AtomicIntegerArray grown = new AtomicIntegerArray(Math.max(blockID + 1, cache.length() * 2));
                for (int i = 0; i < cache.length(); i++) grown.set(i, cache.get(i));
                adventuraarcanacore$byBlockId = cache = grown;
            }
            return cache;
        }
    }

    /** What Better Foliage's rules say, the first that takes the block deciding. */
    @Unique
    private boolean adventuraarcanacore$evaluate(Block block, int metadata) {
        for (Object rule : rules) {
            AccessorBlockFilterRule filter = (AccessorBlockFilterRule) rule;
            if (filter.adventuraarcanacore$matchesBlock(block, metadata))
                return !filter.adventuraarcanacore$isBlockingRule();
        }
        return false;
    }
}
