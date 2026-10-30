package industries.angelic.adventuraarcanacore.mixins.late.bettersleeping;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import cpw.mods.fml.common.gameevent.TickEvent;
import cz.ondraster.bettersleeping.Config;
import cz.ondraster.bettersleeping.EventHandlers;
import cz.ondraster.bettersleeping.api.PlayerData;
import cz.ondraster.bettersleeping.logic.Alarm;
import cz.ondraster.bettersleeping.logic.AlternateSleep;

/**
 * Better Sleeping 1.2.27 checks its share of sleeping players only when someone gets into bed, before that player is
 * asleep, and otherwise waits for everyone like vanilla. Check the share every world tick instead. Also its fix after
 * 1.2.27: lying in bed no longer makes a player tired while it rests them.
 */
@Mixin(value = EventHandlers.class, remap = false)
public abstract class MixinBetterSleepingEvents {

    @Inject(method = "onPreWorldTick", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$sleepByShare(TickEvent.WorldTickEvent event, CallbackInfo ci) {
        if (event.phase != TickEvent.Phase.START || !(event.world instanceof WorldServer)) return;
        ci.cancel();
        if (adventuraarcanacore$enoughAsleep(event.world)) Alarm.sleepWorld(event.world);
    }

    @WrapWithCondition(
        method = "onPlayerTick",
        at = @At(value = "INVOKE", target = "Lcz/ondraster/bettersleeping/api/PlayerData;decreaseSleepLevel()V"),
        remap = false,
        require = 1)
    private boolean adventuraarcanacore$restInBed(PlayerData data,
        @Local(argsOnly = true) TickEvent.PlayerTickEvent event) {
        return !event.player.isPlayerSleeping();
    }

    /** A share above 1 can't be met, so it falls back to everyone, as before. */
    private static boolean adventuraarcanacore$enoughAsleep(World world) {
        int asleep = AlternateSleep.getSleepingPeopleInWorld(world);
        return asleep > 0 && asleep >= world.playerEntities.size() * Math.min(Config.percentPeopleToSleep, 1.0);
    }
}
