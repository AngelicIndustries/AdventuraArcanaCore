package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Starts and stops a toaster without linking Cooking for Blockheads. */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.tile.TileToaster", remap = false)
public interface AccessorToaster {

    @Invoker(value = "setActive", remap = false)
    void adventuraarcanacore$setActive(boolean active);
}
