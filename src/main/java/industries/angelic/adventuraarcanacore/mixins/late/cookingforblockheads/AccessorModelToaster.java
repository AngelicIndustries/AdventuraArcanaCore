package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.client.model.ModelRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The toaster model's lever, read without linking Cooking for Blockheads. */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.client.model.ModelToaster", remap = false)
public interface AccessorModelToaster {

    @Accessor(value = "ToasterButtonThingy", remap = false)
    ModelRenderer adventuraarcanacore$lever();
}
