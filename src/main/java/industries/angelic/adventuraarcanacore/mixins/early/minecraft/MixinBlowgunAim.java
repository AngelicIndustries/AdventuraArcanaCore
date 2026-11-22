package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.adventuraarcanacore.compat.mowziesmobs.BlowgunAim;

/** Holds Mowzie's Mobs' blowgun level from the mouth while it is blown, not in the bow's drawing pose. */
@Mixin(ItemRenderer.class)
public abstract class MixinBlowgunAim {

    @Shadow
    private ItemStack itemToRender;

    @WrapOperation(
        method = "renderItemInFirstPerson",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItemUseAction()Lnet/minecraft/item/EnumAction;"),
        require = 1)
    private EnumAction adventuraarcanacore$noBowPose(ItemStack stack, Operation<EnumAction> original) {
        return BlowgunAim.blowing(stack) ? EnumAction.none : original.call(stack);
    }

    // Forge's renderItem overload is not obfuscated.
    @Inject(
        method = "renderItemInFirstPerson",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V",
            remap = false),
        require = 1)
    private void adventuraarcanacore$aim(float partialTicks, CallbackInfo callback) {
        if (BlowgunAim.blowing(itemToRender)) BlowgunAim.aim();
    }
}
