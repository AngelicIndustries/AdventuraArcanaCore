package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.adventuraarcanacore.compat.chests.ChestPairs;

/**
 * A chest placed while sneaking pairs only with the chest it was placed against, if any, instead of the neighbor
 * AdjaChest would pick. Applied after AdjaChest, whose placement this replaces for it.
 */
@Mixin(value = BlockChest.class, priority = 1100)
public abstract class MixinBlockChestSneakPlace {

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"), cancellable = true)
    private void adventuraarcanacore$sneakPlace(World world, int x, int y, int z, EntityLivingBase placer,
        ItemStack stack, CallbackInfo ci) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityChest) || !ChestPairs.sneaking(placer)) return;
        ChestPairs.Pairable chest = (ChestPairs.Pairable) tile;
        chest.adventuraarcanacore$placed(stack);
        ChestPairs.sneakPlaced(world, x, y, z, placer, chest);
        ((TileEntityChest) tile).checkForAdjacentChests();
        ci.cancel();
    }
}
