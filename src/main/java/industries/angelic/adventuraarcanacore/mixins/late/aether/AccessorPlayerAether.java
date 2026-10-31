package industries.angelic.adventuraarcanacore.mixins.late.aether;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

/** A player's Aether data, read without linking the Aether. */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.player.PlayerAether", remap = false)
public interface AccessorPlayerAether {

    /** Where the player's wings are in their beat, in radians. */
    @Accessor(value = "wingSinage", remap = false)
    float adventuraarcanacore$getWingSinage();
}
