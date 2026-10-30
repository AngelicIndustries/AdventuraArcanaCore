package industries.angelic.adventuraarcanacore.mixins.late.bettersleeping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import cz.ondraster.bettersleeping.Config;
import cz.ondraster.bettersleeping.client.gui.SleepOverlay;

/**
 * Better Sleeping draws its sleep bar at guiOffsetLeft but the bar's marker at a fixed 4 px (its issue #34). Client
 * only.
 */
@Mixin(value = SleepOverlay.class, remap = false)
public abstract class MixinSleepOverlay {

    @ModifyConstant(method = "onGuiRender", constant = @Constant(intValue = 4, ordinal = 0), remap = false, require = 1)
    private int adventuraarcanacore$markerFollowsBar(int left) {
        return Config.guiOffsetLeft;
    }
}
