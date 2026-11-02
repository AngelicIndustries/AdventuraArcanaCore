package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.audio.SoundManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import paulscode.sound.SoundSystem;

/** Sound cleanup stops during singleplayer pause, leaving finished menu clicks in playingSounds. */
@Mixin(SoundManager.class)
public abstract class MixinSoundManagerPause {

    @Unique
    private final Set<String> adventuraarcanacore$pausedSounds = new HashSet<>();

    @WrapWithCondition(
        method = "pauseAllSounds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;pause(Ljava/lang/String;)V",
            remap = false),
        require = 1)
    private boolean adventuraarcanacore$rememberPausedSound(@Coerce SoundSystem system, String channel) {
        // Minecraft's playing() includes paused and preloaded sources. Do not pause finished sources.
        if (!system.playing(channel)) return false;
        adventuraarcanacore$pausedSounds.add(channel);
        return true;
    }

    @WrapWithCondition(
        method = "resumeAllSounds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;play(Ljava/lang/String;)V",
            remap = false),
        require = 1)
    private boolean adventuraarcanacore$resumePausedSound(@Coerce SoundSystem system, String channel) {
        return adventuraarcanacore$pausedSounds.remove(channel) && system.playing(channel);
    }

    @Inject(method = { "resumeAllSounds", "stopAllSounds" }, at = @At("RETURN"), require = 1)
    private void adventuraarcanacore$clearPausedSounds(CallbackInfo ci) {
        adventuraarcanacore$pausedSounds.clear();
    }
}
