package industries.angelic.adventuraarcanacore.mixins.late.reccomplex;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import industries.angelic.adventuraarcanacore.compat.reccomplex.NaturalFillQueue;

/**
 * The natural fill under a structure queues each column for the layer below with addIfNew, whose contains() never
 * matches an int[]. Columns outside the structure's box are never filled, so their copies kept spreading, and one
 * structure could hold the server thread for seconds. Its two queues dedupe by coordinates instead.
 */
@Pseudo
@Mixin(targets = "ivorius.reccomplex.structures.generic.transformers.TransformerNatural", remap = false)
public abstract class MixinTransformerNatural {

    @Redirect(
        method = "transformBlock(Livorius/reccomplex/utils/NBTNone;Livorius/reccomplex/structures/generic/transformers/Transformer$Phase;Livorius/reccomplex/structures/StructureSpawnContext;Livorius/ivtoolkit/blocks/BlockCoord;Livorius/reccomplex/utils/IBlockState;)V",
        at = @At(value = "NEW", target = "java/util/ArrayList"),
        require = 2)
    private ArrayList<int[]> adventuraarcanacore$columnQueue() {
        return new NaturalFillQueue();
    }
}
