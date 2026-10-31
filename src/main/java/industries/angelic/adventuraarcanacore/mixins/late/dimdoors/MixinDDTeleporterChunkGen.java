package industries.angelic.adventuraarcanacore.mixins.late.dimdoors;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.dimdev.dimdoors.core.DDTeleporter;
import org.dimdev.dimdoors.core.DimLink;
import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

/**
 * Hodgepodge's preventEntityChunkLoading hands out an empty dummy chunk for any chunk that would be generated during
 * entity ticks, in every world. A mob walking into a door does its traversal in its tick, so Dimensional Doors built
 * the new pocket's dungeon into the dummy chunk, found no door where it placed one and crashed. Allow chunk
 * generation for the traversal and put the guard back afterwards.
 */
@Mixin(value = DDTeleporter.class, remap = false)
public abstract class MixinDDTeleporterChunkGen {

    @WrapMethod(method = "traverseDimDoor")
    private static void adventuraarcanacore$allowChunkGen(World world, DimLink link, Entity entity, Block door,
        Operation<Void> original) {
        if (!ChunkGenScheduler.isBlocked()) {
            original.call(world, link, entity, door);
            return;
        }
        ChunkGenScheduler.enableChunkLoads();
        try {
            original.call(world, link, entity, door);
        } finally {
            ChunkGenScheduler.disableChunkLoads();
        }
    }
}
