package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.Toasts;

@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.registry.CookingRegistry", remap = false)
public abstract class MixinToasterRecipes {

    @Inject(method = "getToastOutput", at = @At("RETURN"), cancellable = true, require = 1)
    private static void adventuraarcanacore$breadToast(ItemStack input, CallbackInfoReturnable<ItemStack> result) {
        ItemStack toast = Toasts.output(input);
        result.setReturnValue(toast != null ? toast : Toasts.replaceOldToast(result.getReturnValue()));
    }
}
