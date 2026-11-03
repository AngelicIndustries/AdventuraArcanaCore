package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.ToasterLever;

/**
 * Cooking for Blockheads moved the toaster model's lever after drawing it, and every toaster shares that model, so
 * each toaster showed the lever of the one drawn before. The lever and the bread now follow each toaster's own,
 * smoothly moving lever.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.client.render.tile.TileEntityToasterRenderer", remap = false)
public abstract class MixinToasterLeverRender {

    @Unique
    private float adventuraarcanacore$lever;

    @Inject(method = { "renderTileEntityAt", "func_147500_a" }, at = @At("HEAD"), remap = false, require = 1)
    private void adventuraarcanacore$readLever(TileEntity tile, double x, double y, double z, float partialTicks,
        CallbackInfo ci) {
        // The inventory's toaster is not in a world and stays up.
        adventuraarcanacore$lever = tile instanceof ToasterLever.Animated && tile.hasWorldObj()
            ? ((ToasterLever.Animated) tile).adventuraarcanacore$lever(partialTicks)
            : 0;
    }

    @WrapOperation(
        method = { "renderTileEntityAt", "func_147500_a" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/blay09/mods/cookingforblockheads/client/model/ModelToaster;renderAll()V",
            remap = false),
        remap = false,
        require = 1)
    private void adventuraarcanacore$placeLever(@Coerce Object model, Operation<Void> original) {
        ((AccessorModelToaster) model).adventuraarcanacore$lever().offsetY = ToasterLever.BUTTON_TRAVEL
            * adventuraarcanacore$lever;
        original.call(model);
    }

    // The two bread slots, drawn after the model.
    @ModifyArg(
        method = { "renderTileEntityAt", "func_147500_a" },
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/blay09/mods/cookingforblockheads/client/model/ModelToaster;renderAll()V",
                remap = false)),
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", remap = false),
        index = 1,
        remap = false,
        require = 2)
    private float adventuraarcanacore$placeBread(float y) {
        return ToasterLever.breadY(adventuraarcanacore$lever);
    }
}
