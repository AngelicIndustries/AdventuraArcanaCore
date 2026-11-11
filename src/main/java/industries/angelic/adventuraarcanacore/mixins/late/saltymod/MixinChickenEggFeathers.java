package industries.angelic.adventuraarcanacore.mixins.late.saltymod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * SaltyMod sheds feathers when a chicken lays an egg, and takes any rise of its egg timer since the last tick for one.
 * Hunger Overhaul slows egg laying by adding to the timer on most ticks; a baby chicken's timer never counts down, so
 * it rose nearly every tick and babies shed feathers without end. A laid egg sets the timer back thousands of ticks, so
 * only a rise of more than EGG_RISE counts: SaltyMod compares the timer with the one it remembered, read here.
 */
@Pseudo
@Mixin(targets = "darkbum.saltymod.event.LivingUpdateEventHandler", remap = false)
public abstract class MixinChickenEggFeathers {

    /** More than any mod adds to the timer in a tick, far less than a laid egg's reset (6000 to 12000 ticks). */
    @Unique
    private static final int adventuraarcanacore$EGG_RISE = 100;

    @Redirect(
        method = "onLivingUpdate",
        at = @At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I"),
        remap = false,
        require = 1)
    private int adventuraarcanacore$eggLaid(Integer last) {
        return last + adventuraarcanacore$EGG_RISE;
    }
}
