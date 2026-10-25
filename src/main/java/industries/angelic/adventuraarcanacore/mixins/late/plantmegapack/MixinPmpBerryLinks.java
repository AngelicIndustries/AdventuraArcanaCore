package industries.angelic.adventuraarcanacore.mixins.late.plantmegapack;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.food.FoodLinks;

/** PMP's blackberry, blueberry and strawberry bushes give HarvestCraft's berries when HarvestCraft is installed. */
@Pseudo
@Mixin(targets = "plantmegapack.block.PMPBlockPlant", remap = false)
public abstract class MixinPmpBerryLinks {

    @Inject(method = "getBerryItemStack", at = @At("RETURN"), cancellable = true, require = 1, remap = false)
    private void adventuraarcanacore$harvestCraftBerries(int metadata, int quantity,
        CallbackInfoReturnable<ItemStack> result) {
        ItemStack berries = result.getReturnValue();
        if (berries == null) return;
        Item linked = FoodLinks.berry(
            ((Block) (Object) this).getUnlocalizedName()
                .substring(5));
        if (linked != null) result.setReturnValue(new ItemStack(linked, berries.stackSize));
    }
}
