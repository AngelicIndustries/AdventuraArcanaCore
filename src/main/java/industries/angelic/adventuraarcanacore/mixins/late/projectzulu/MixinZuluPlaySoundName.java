package industries.angelic.adventuraarcanacore.mixins.late.projectzulu;

import net.minecraft.command.ICommandSender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Project Zulu's dungeon module registers its own /playsound after vanilla's, replacing it: player before sound, a
 * range instead of volume and pitch, and still vanilla's usage text, so /playsound <sound> <player> looked up the
 * sound as a player. It is now /zuluplaysound, with its own usage, and /playsound is vanilla's again.
 */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.dungeon.commands.CommandPlaySound", remap = false)
public abstract class MixinZuluPlaySoundName {

    @Inject(
        method = { "getCommandName", "func_71517_b" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$name(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("zuluplaysound");
    }

    @Inject(
        method = { "getCommandUsage", "func_71518_a" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$usage(ICommandSender sender, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("/zuluplaysound <player> <sound> [<x> <y> <z>] [range]");
    }
}
