package industries.angelic.adventuraarcanacore.mixins.late.biomesoplenty;

import net.minecraftforge.client.event.GuiOpenEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Biomes O' Plenty puts a screen asking for its own world type in front of Create World, once per player. The pack
 * sets the world type itself, so skip it. Client only.
 */
@Pseudo
@Mixin(targets = "biomesoplenty.common.eventhandler.client.gui.WorldTypeMessageEventHandler", remap = false)
public abstract class MixinWorldTypeScreen {

    @Inject(method = "openCreateWorld", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$skip(GuiOpenEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
