package industries.angelic.adventuraarcanacore.mixins.late.structurememory;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

/** One of a Structure Memory structure kind's rules: the blocks it needs. */
@Pseudo
@Mixin(targets = "xsoras.structurememory.memory.StructureKind$Rule", remap = false)
public interface AccessorStructureRule {

    @Accessor(value = "requirements", remap = false)
    List<?> adventuraarcanacore$requirements();
}
