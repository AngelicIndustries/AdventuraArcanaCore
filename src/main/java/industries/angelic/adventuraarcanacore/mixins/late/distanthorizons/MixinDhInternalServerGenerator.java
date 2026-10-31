package industries.angelic.adventuraarcanacore.mixins.late.distanthorizons;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.ForgeChunkManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.seibel.distanthorizons.common.backports.ChunkPos;
import com.seibel.distanthorizons.common.util.threading.ServerThreadTaskHandler;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper;
import com.seibel.distanthorizons.common.wrappers.modAccessor.IHodgePodgeCommonAccessor;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.DhInternalServerGenerator;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.params.GlobalWorldGenParams;
import com.seibel.distanthorizons.core.api.internal.chunkUpdating.ChunkUpdateQueueManager;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.pos.DhChunkPos;

/**
 * Distant Horizons generates a chunk for its LODs as one server task that loads the chunk's eight neighbours and then
 * the chunk, and its task handler checks the tick's budget only between tasks. In fresh terrain one task generated
 * nine chunks in one tick, and the server then ran its catch-up ticks back to back: seconds of lag in a
 * pre-generation. Each chunk is its own task here, neighbours first (the queue is first in, first out), so the budget
 * applies between chunks. The returned future still fails if any of the nine loads failed.
 */
@Mixin(value = DhInternalServerGenerator.class, remap = false)
public abstract class MixinDhInternalServerGenerator {

    @Shadow
    @Final
    private static IHodgePodgeCommonAccessor HODGE_PODGE_ACCESSOR;

    @Shadow
    @Final
    private GlobalWorldGenParams params;

    @Shadow
    @Final
    private IDhServerLevel dhServerLevel;

    @Shadow
    @Final
    private ChunkUpdateQueueManager updateManager;

    @Shadow
    @Final
    private ForgeChunkManager.Ticket dhServerGenTicket;

    @Shadow
    private boolean acquireChunkRef(DhChunkPos chunkPos) {
        throw new AssertionError();
    }

    @Inject(method = "requestChunkFromServerAsync", at = @At("HEAD"), cancellable = true, require = 1)
    private void adventuraarcanacore$chunkTasks(ChunkPos chunkPos,
        CallbackInfoReturnable<CompletableFuture<ChunkWrapper>> result) {
        WorldServer level = params.mcServerLevel;
        if (updateManager != null) updateManager.addPosToIgnore(McObjectConverter.convert(chunkPos));
        ArrayList<CompletableFuture<Object>> neighbours = new ArrayList<>(8);
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            if (dx == 0 && dz == 0) continue;
            int x = chunkPos.x + dx, z = chunkPos.z + dz;
            neighbours.add(ServerThreadTaskHandler.INSTANCE.queueTask(() -> {
                // Adjacent generation events share border chunks: only the first reference loads one.
                if (adventuraarcanacore$acquire(level, x, z)) {
                    ChunkProviderServer provider = (ChunkProviderServer) level.getChunkProvider();
                    if (!provider.chunkExists(x, z)) provider.loadChunk(x, z);
                }
                return null;
            }));
        }
        CompletableFuture<ChunkWrapper> target = ServerThreadTaskHandler.INSTANCE.queueTask(() -> {
            adventuraarcanacore$acquire(level, chunkPos.x, chunkPos.z);
            Chunk chunk = ((ChunkProviderServer) level.getChunkProvider()).loadChunk(chunkPos.x, chunkPos.z);
            return chunk == null ? null : new ChunkWrapper(chunk, dhServerLevel.getLevelWrapper());
        });
        result.setReturnValue(
            CompletableFuture.allOf(neighbours.toArray(new CompletableFuture[0]))
                .thenCombine(target, (ignored, chunk) -> chunk));
    }

    /** The original's per-chunk bookkeeping; on the server thread. True if this was the first reference. */
    private boolean adventuraarcanacore$acquire(WorldServer level, int x, int z) {
        if (!acquireChunkRef(new DhChunkPos(x, z))) return false;
        if (updateManager != null) updateManager.addPosToIgnore(new DhChunkPos(x, z));
        if (HODGE_PODGE_ACCESSOR != null) HODGE_PODGE_ACCESSOR.preventChunkSimulation(level, x, z);
        ForgeChunkManager.forceChunk(dhServerGenTicket, new ChunkCoordIntPair(x, z));
        return true;
    }
}
