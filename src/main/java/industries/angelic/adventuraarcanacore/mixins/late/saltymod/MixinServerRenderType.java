package industries.angelic.adventuraarcanacore.mixins.late.saltymod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Four SaltyMod blocks read their render type from the client proxy, and vanilla's entity movement asks for it on a
 * dedicated server too. None of them needs the fence, wall or gate render type there; their bounds and collision stay
 * their own. Dedicated server only.
 */
@Pseudo
@Mixin(
    targets = { "darkbum.saltymod.block.BlockSaltGrass", "darkbum.saltymod.block.BlockCookingPot",
        "darkbum.saltymod.block.BlockMarshReeds$BlockMarshReedsBottom",
        "darkbum.saltymod.block.BlockMarshReeds$BlockMarshReedsTop" },
    remap = false)
public abstract class MixinServerRenderType {

    // The target is not on the compile classpath: name its MCP and shipped SRG methods.
    @Inject(
        method = { "getRenderType", "func_149645_b" },
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        remap = false)
    private void adventuraarcanacore$serverRenderType(CallbackInfoReturnable<Integer> callback) {
        callback.setReturnValue(0);
    }
}
