package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.entity.player.EntityPlayer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import industries.angelic.adventuraarcanacore.compat.aether.AetherGround;

/**
 * The Aether beats a player's wings slowly while they stand and fast in the air. A client never learns whether other
 * players stand, so their wings always beat fast there. They now beat slowly while something is under their feet.
 */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.player.PlayerAether", remap = false)
public abstract class MixinWingBeat {

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/entity/player/EntityPlayer;onGround:Z",
            remap = true),
        remap = false,
        require = 1)
    private boolean adventuraarcanacore$standing(EntityPlayer player) {
        return AetherGround.standing(player);
    }
}
