package industries.angelic.adventuraarcanacore.mixins.late.etfuturum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import ganymedes01.etfuturum.core.handlers.ClientEventHandler;

/**
 * Et Futurum warns in chat about its experimental features when a player joins, and Biomic turns some of them on for
 * the pack. Skip the two chat lines; the warning stays in the log. Client only.
 */
@Mixin(value = ClientEventHandler.class, remap = false)
public abstract class MixinExperimentalWarning {

    @WrapWithCondition(
        method = "onClientTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;addChatComponentMessage(Lnet/minecraft/util/IChatComponent;)V",
            remap = true),
        slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=efr.warn.experimental"),
            to = @At(value = "CONSTANT", args = "stringValue=efr.log.experimental")),
        require = 2)
    private boolean adventuraarcanacore$noChatWarning(EntityPlayer player, IChatComponent message) {
        return false;
    }
}
