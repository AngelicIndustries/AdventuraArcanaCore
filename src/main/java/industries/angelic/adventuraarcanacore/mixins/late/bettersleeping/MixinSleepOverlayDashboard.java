package industries.angelic.adventuraarcanacore.mixins.late.bettersleeping;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cz.ondraster.bettersleeping.client.gui.SleepOverlay;
import industries.angelic.adventuraarcanacore.compat.bettersleeping.BetterSleepingDashboard;

/**
 * Moves Better Sleeping's sleep bar, and the Ring Watch time under it, where the player put them in Dashboard's HUD
 * editor (BetterSleepingDashboard). Client only.
 */
@Mixin(value = SleepOverlay.class, remap = false)
public abstract class MixinSleepOverlayDashboard {

    @Unique
    private boolean adventuraarcanacore$watchMoved;

    /** From binding the bar's texture to just before the time: the bar, its marker and the bed. */
    @Inject(
        method = "onGuiRender",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V",
            ordinal = 0,
            remap = true),
        require = 1)
    private void adventuraarcanacore$moveBar(RenderGameOverlayEvent event, CallbackInfo ci) {
        BetterSleepingDashboard.beginBar();
    }

    @Inject(
        method = "onGuiRender",
        at = @At(
            value = "INVOKE",
            target = "Lcz/ondraster/bettersleeping/client/gui/SleepOverlay;renderTimeOverlay()V"),
        require = 1)
    private void adventuraarcanacore$barDrawn(RenderGameOverlayEvent event, CallbackInfo ci) {
        BetterSleepingDashboard.endBar();
    }

    /** Only there with Baubles, which renderTimeOverlay needs, so it's not required. */
    @Inject(method = "renderTimeOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void adventuraarcanacore$moveWatch(CallbackInfo ci) {
        adventuraarcanacore$watchMoved = false;
        // Without the watch nothing is drawn, so Dashboard shows its sample instead
        if (!BetterSleepingDashboard.wearsRingWatch()) {
            return;
        }
        if (!BetterSleepingDashboard.beginWatch()) {
            ci.cancel();
            return;
        }
        adventuraarcanacore$watchMoved = true;
    }

    @Inject(method = "renderTimeOverlay", at = @At("RETURN"), require = 0)
    private void adventuraarcanacore$watchDrawn(CallbackInfo ci) {
        if (adventuraarcanacore$watchMoved) {
            adventuraarcanacore$watchMoved = false;
            BetterSleepingDashboard.endWatch();
        }
    }
}
