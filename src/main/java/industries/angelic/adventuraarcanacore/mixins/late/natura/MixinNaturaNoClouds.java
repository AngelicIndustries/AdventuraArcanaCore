package industries.angelic.adventuraarcanacore.mixins.late.natura;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** No Natura clouds anywhere (white, dark, ash and sulfur, in every dimension): they float as flat slabs. */
@Pseudo
@Mixin(targets = "mods.natura.worldgen.CloudGen", remap = false)
public abstract class MixinNaturaNoClouds {

    @Inject(
        method = { "generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
            "func_76484_a(Lnet/minecraft/world/World;Ljava/util/Random;III)Z" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$noCloud(World world, Random random, int x, int y, int z,
        CallbackInfoReturnable<Boolean> result) {
        result.setReturnValue(false);
    }
}
