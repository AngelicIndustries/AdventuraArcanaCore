package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.food.WildCrops;

/** HarvestCraft's and Harvest the Nether's crops stay where they grow wild (grass, sand, netherrack). */
@Mixin(BlockBush.class)
public abstract class MixinBlockBushWildCrops {

    @Inject(method = "canBlockStay", at = @At("HEAD"), cancellable = true, require = 1)
    private void adventuraarcanacore$wildCrop(World world, int x, int y, int z,
        CallbackInfoReturnable<Boolean> result) {
        if (WildCrops.stays((Block) (Object) this, world, x, y, z)) result.setReturnValue(true);
    }
}
