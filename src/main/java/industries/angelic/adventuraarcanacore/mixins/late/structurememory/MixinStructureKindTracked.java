package industries.angelic.adventuraarcanacore.mixins.late.structurememory;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

/**
 * Structure Memory asks for every block of every chunk it scans whether a structure rule names it, and answered by
 * walking every rule of every structure kind, copying the kinds' array (StructureKind.values()) each time: a fifth of
 * the client thread's time in the pack. The rules are fixed and name vanilla blocks only, so the blocks they name, with
 * their metadata, are gathered once into a table. Client only.
 */
@Pseudo
@Mixin(targets = "xsoras.structurememory.memory.StructureKind", remap = false)
public abstract class MixinStructureKindTracked {

    /** Each block the rules name, with a bit for each metadata they name; all bits where any metadata will do. */
    @Unique
    private static Map<Block, Integer> adventuraarcanacore$tracked;

    /**
     * @author jack
     * @reason It runs for every block of every chunk scanned, and the rules it walked never change. Only the client
     *         thread scans.
     */
    @Overwrite(remap = false)
    public static boolean isTracked(Block block, int meta) {
        Map<Block, Integer> tracked = adventuraarcanacore$tracked;
        if (tracked == null) adventuraarcanacore$tracked = tracked = adventuraarcanacore$gather();
        Integer metas = tracked.get(block);
        return metas != null && (metas == -1 || meta >= 0 && meta < Integer.SIZE && (metas >>> meta & 1) != 0);
    }

    @Unique
    private static Map<Block, Integer> adventuraarcanacore$gather() {
        Object[] kinds;
        try {
            kinds = Class.forName("xsoras.structurememory.memory.StructureKind")
                .getEnumConstants();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        Map<Block, Integer> tracked = new IdentityHashMap<>();
        for (Object kind : kinds) for (Object rule : ((AccessorStructureKind) kind).adventuraarcanacore$rules())
            for (Object needed : ((AccessorStructureRule) rule).adventuraarcanacore$requirements()) {
                AccessorStructureRequirement requirement = (AccessorStructureRequirement) needed;
                int meta = requirement.adventuraarcanacore$meta();
                tracked.merge(requirement.adventuraarcanacore$block(), meta < 0 ? -1 : 1 << meta, (a, b) -> a | b);
            }
        return tracked;
    }
}
