package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Shift-clicking the creative inventory's Destroy Item slot clears the whole inventory, but its tooltip still read
 * "Destroy Item". With shift held it now reads "Clear Inventory".
 */
@Mixin(GuiContainerCreative.class)
public abstract class MixinCreativeClearInventory {

    @ModifyArg(
        method = "drawScreen",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
        index = 0,
        require = 1)
    private String adventuraarcanacore$clearInventory(String key) {
        return "inventory.binSlot".equals(key) && GuiScreen.isShiftKeyDown()
            ? "adventuraarcanacore.inventory.clearInventory"
            : key;
    }
}
