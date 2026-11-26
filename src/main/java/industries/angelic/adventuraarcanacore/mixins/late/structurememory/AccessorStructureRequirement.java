package industries.angelic.adventuraarcanacore.mixins.late.structurememory;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

/** A block a Structure Memory rule needs, and its metadata (negative for any). */
@Pseudo
@Mixin(targets = "xsoras.structurememory.memory.StructureKind$Requirement", remap = false)
public interface AccessorStructureRequirement {

    @Accessor(value = "block", remap = false)
    Block adventuraarcanacore$block();

    @Accessor(value = "meta", remap = false)
    int adventuraarcanacore$meta();
}
