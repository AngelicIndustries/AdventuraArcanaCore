package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.compat.aether.AetherGround;

/**
 * The Aether poses every player's wings from one shared beat, set only after posing, so each player's wings flap with
 * the beat of whoever was drawn before them. They are now posed from their own player's beat, and spread as wide as
 * that player standing or flying calls for, which {@link MixinWingBeat} works out for other players too.
 */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.client.models.attachments.ModelAetherWings", remap = false)
public abstract class MixinWingFlap {

    private static final String PLAYER_AETHER = "aether_legacy:player_aether";

    @Shadow(remap = false)
    public float sinage;

    @Inject(method = { "setRotationAngles", "func_78087_a" }, at = @At("HEAD"), remap = false, require = 1)
    private void adventuraarcanacore$ownBeat(float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw,
        float headPitch, float scale, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityPlayer)) return;
        IExtendedEntityProperties aether = entity.getExtendedProperties(PLAYER_AETHER);
        if (aether instanceof AccessorPlayerAether)
            sinage = ((AccessorPlayerAether) aether).adventuraarcanacore$getWingSinage();
    }

    @Redirect(
        method = { "setRotationAngles", "func_78087_a" },
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/entity/Entity;onGround:Z",
            remap = true),
        remap = false,
        require = 1)
    private boolean adventuraarcanacore$standing(Entity entity) {
        return AetherGround.standing(entity);
    }
}
