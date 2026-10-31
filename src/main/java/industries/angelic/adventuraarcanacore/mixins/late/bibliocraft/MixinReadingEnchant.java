package industries.angelic.adventuraarcanacore.mixins.late.bibliocraft;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * BiblioCraft's Reading enchant gives a helmet what its Reading Glasses do, and its config can't turn it off. The pack
 * has no glasses, so the enchant can't be had either: not from the enchanting table, on books or on an anvil. It stays
 * registered, so helmets that have it keep loading.
 */
@Pseudo
@Mixin(targets = "jds.bibliocraft.EnchantmentReading", remap = false)
public abstract class MixinReadingEnchant extends Enchantment {

    private MixinReadingEnchant(int id, int weight, EnumEnchantmentType type) {
        super(id, weight, type);
    }

    @Inject(method = "canApplyAtEnchantingTable", at = @At("HEAD"), cancellable = true, require = 1)
    private void adventuraarcanacore$notAtTable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
