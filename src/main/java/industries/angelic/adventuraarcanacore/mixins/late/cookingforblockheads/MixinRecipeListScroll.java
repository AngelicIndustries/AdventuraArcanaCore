package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.KitchenHands;

/**
 * The recipe book and cooking table scrolled their recipe list with the mouse wheel anywhere on screen, including
 * over the player's inventory and NEI. The wheel now scrolls it only over the list or its scrollbar.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.client.GuiRecipeBook", remap = false)
public abstract class MixinRecipeListScroll extends GuiContainer {

    private MixinRecipeListScroll(Container container) {
        super(container);
    }

    @ModifyExpressionValue(
        method = { "handleMouseInput", "func_146274_d" },
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I", remap = false),
        remap = false,
        require = 1)
    private int adventuraarcanacore$overList(int delta) {
        if (delta == 0) return 0;
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        return KitchenHands.overRecipeList(mouseX - guiLeft, mouseY - guiTop) ? delta : 0;
    }
}
