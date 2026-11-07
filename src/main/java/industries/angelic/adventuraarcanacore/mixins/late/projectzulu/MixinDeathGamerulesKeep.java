package industries.angelic.adventuraarcanacore.mixins.late.projectzulu;

import net.minecraft.world.GameRules;
import net.minecraftforge.event.world.WorldEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Project Zulu set keepInventory and its own dropInventory, dropHotbar, dropArmor and dropXP gamerules to its config
 * defaults whenever a world loaded, undoing any /gamerule change. It now only adds its own gamerules, with those
 * defaults, to a world that lacks them, and leaves keepInventory alone.
 */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.DeathGamerules", remap = false)
public abstract class MixinDeathGamerulesKeep {

    @Shadow(remap = false)
    boolean dropInventoryDefault;
    @Shadow(remap = false)
    boolean dropHotbarDefault;
    @Shadow(remap = false)
    boolean dropArmorDefault;
    @Shadow(remap = false)
    boolean dropXPDefault;

    @Inject(method = "worldLoad", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$addMissingOnly(WorldEvent.Load event, CallbackInfo ci) {
        GameRules rules = event.world.getGameRules();
        adventuraarcanacore$add(rules, "dropInventory", dropInventoryDefault);
        adventuraarcanacore$add(rules, "dropHotbar", dropHotbarDefault);
        adventuraarcanacore$add(rules, "dropArmor", dropArmorDefault);
        adventuraarcanacore$add(rules, "dropXP", dropXPDefault);
        ci.cancel();
    }

    @Unique
    private static void adventuraarcanacore$add(GameRules rules, String name, boolean value) {
        if (!rules.hasRule(name)) rules.addGameRule(name, Boolean.toString(value));
    }
}
