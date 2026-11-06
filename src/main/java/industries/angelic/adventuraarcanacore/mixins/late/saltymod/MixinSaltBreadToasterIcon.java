package industries.angelic.adventuraarcanacore.mixins.late.saltymod;

import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.ToasterBreadLook;

/**
 * Salted bread's salt particles sit outside the loaf in its texture, so in Cooking for Blockheads' toaster they hung
 * in the air beside the slot. The toaster draws the bread without them. Client only.
 */
@Pseudo
@Mixin(targets = "darkbum.saltymod.item.ItemSaltFoodBase", remap = false)
public abstract class MixinSaltBreadToasterIcon {

    // The target is not on the compile classpath: name its MCP and shipped SRG methods.
    @Inject(
        method = { "getIconFromDamage", "func_77617_a" },
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        remap = false)
    private void adventuraarcanacore$toasterIcon(int meta, CallbackInfoReturnable<IIcon> callback) {
        IIcon icon = ToasterBreadLook.icon((Item) (Object) this, meta);
        if (icon != null) callback.setReturnValue(icon);
    }
}
