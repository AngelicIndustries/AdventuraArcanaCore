package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.KitchenHands;

/**
 * Bread loaded into the toaster could only come out toasted. Sneak-using the toaster with an empty hand now takes the
 * bread back and stops it. An empty toaster starts and stops as before.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.block.BlockToaster", remap = false)
public abstract class MixinToasterTakeBread {

    @Inject(
        method = { "onBlockActivated", "func_149727_a" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$takeBread(World world, int x, int y, int z, EntityPlayer player, int side,
        float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> result) {
        if (!player.isSneaking() || player.getHeldItem() != null) return;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof IInventory) || !(tile instanceof AccessorToaster)) return;
        // Both sides take it: the toaster's running state is never synced, so each side stops its own.
        if (!KitchenHands.takeAll((IInventory) tile, player)) return;
        ((AccessorToaster) tile).adventuraarcanacore$setActive(false);
        result.setReturnValue(true);
    }
}
