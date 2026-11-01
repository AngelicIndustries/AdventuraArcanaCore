package industries.angelic.adventuraarcanacore.mixins.late.adventurebackpack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import codechicken.lib.render.CCRenderState;

/**
 * Adventure Backpack draws the fluid in a worn backpack's, copter pack's or coal jetpack's tanks through
 * CodeChickenLib's shared render state without resetting it, so the fluid takes the brightness of whatever
 * CodeChickenLib drew last, often dark or black. Reset it and take the wearer's light first, as Adventure Backpack does
 * for a placed backpack's tanks.
 */
@Pseudo
@Mixin(targets = "com.darkona.adventurebackpack.client.models.ModelWearable", remap = false)
public abstract class MixinWornTankLight {

    @Inject(
        method = "renderFluidInTank",
        at = @At(
            value = "INVOKE",
            target = "Lcodechicken/lib/render/RenderUtils;renderFluidCuboid(Lnet/minecraftforge/fluids/FluidStack;Lcodechicken/lib/vec/Cuboid6;DD)V",
            remap = false),
        remap = false,
        require = 1)
    private void adventuraarcanacore$wearerLight(CallbackInfo ci) {
        CCRenderState state = CCRenderState.instance();
        state.resetInstance();
        state.pullLightmapInstance();
        state.useNormals = true;
    }
}
