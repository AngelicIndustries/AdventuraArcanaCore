package industries.angelic.adventuraarcanacore.mixins.late.saltymod;

import net.minecraft.util.IIcon;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

/**
 * SaltyMod's bee nest constructor writes two client-only icon fields. Forge strips the fields on a dedicated server but
 * not their initializers, so the server crashes. Dedicated server only.
 */
@Pseudo
@Mixin(targets = "darkbum.saltymod.block.BlockBeeNest", remap = false)
public abstract class MixinBeeNestServerIcons {

    @WrapWithCondition(
        method = "<init>",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.PUTFIELD,
            target = "Ldarkbum/saltymod/block/BlockBeeNest;temperateIcons:[Lnet/minecraft/util/IIcon;"),
        remap = false,
        require = 1)
    private boolean adventuraarcanacore$skipTemperateIcons(@Coerce Object nest, IIcon[] icons) {
        return false;
    }

    @WrapWithCondition(
        method = "<init>",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.PUTFIELD,
            target = "Ldarkbum/saltymod/block/BlockBeeNest;borealIcons:[Lnet/minecraft/util/IIcon;"),
        remap = false,
        require = 1)
    private boolean adventuraarcanacore$skipBorealIcons(@Coerce Object nest, IIcon[] icons) {
        return false;
    }
}
