package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.ToasterBreadLook;

/** Marks the toaster's drawing, which draws only the toaster and the bread in its slots. */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.client.render.tile.TileEntityToasterRenderer", remap = false)
public abstract class MixinToasterBreadLook {

    @Inject(method = { "renderTileEntityAt", "func_147500_a" }, at = @At("HEAD"), remap = false, require = 1)
    private void adventuraarcanacore$startBread(TileEntity tile, double x, double y, double z, float partialTicks,
        CallbackInfo ci) {
        ToasterBreadLook.drawing(true);
    }

    @Inject(method = { "renderTileEntityAt", "func_147500_a" }, at = @At("RETURN"), remap = false, require = 1)
    private void adventuraarcanacore$endBread(TileEntity tile, double x, double y, double z, float partialTicks,
        CallbackInfo ci) {
        ToasterBreadLook.drawing(false);
    }
}
