package industries.angelic.adventuraarcanacore.mixins.late.structurememory;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

/** A Structure Memory structure kind's rules, each a set of blocks its structure holds. */
@Pseudo
@Mixin(targets = "xsoras.structurememory.memory.StructureKind", remap = false)
public interface AccessorStructureKind {

    @Invoker(value = "rules", remap = false)
    List<?> adventuraarcanacore$rules();
}
