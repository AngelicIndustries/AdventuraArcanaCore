package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Asks the toaster's recipes without linking Cooking for Blockheads. */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.registry.CookingRegistry", remap = false)
public interface AccessorCookingRegistry {

    @Invoker(value = "getToastOutput", remap = false)
    static ItemStack adventuraarcanacore$toastOutput(ItemStack input) {
        throw new AssertionError();
    }
}
