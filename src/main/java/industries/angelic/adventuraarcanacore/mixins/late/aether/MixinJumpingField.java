package industries.angelic.adventuraarcanacore.mixins.late.aether;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import industries.angelic.adventuraarcanacore.compat.aether.AetherJumping;

/**
 * The Aether's per-tick check of whether a player is jumping, through a field found once; see {@link AetherJumping}.
 */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.player.PlayerAether", remap = false)
public abstract class MixinJumpingField {

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lcpw/mods/fml/relauncher/ReflectionHelper;getPrivateValue(Ljava/lang/Class;Ljava/lang/Object;[Ljava/lang/String;)Ljava/lang/Object;",
            remap = false),
        remap = false,
        require = 1)
    private Object adventuraarcanacore$readJumping(Class<?> type, Object instance, String[] names) {
        return AetherJumping.read(type, instance, names);
    }
}
