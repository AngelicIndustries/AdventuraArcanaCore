package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.KitchenHands;

/**
 * Cookware placed on the oven could only come back through its screen. Sneak-using the oven's top with an empty hand
 * now takes the cookware under the cursor. Without cookware there, the oven opens as before.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.block.BlockOven", remap = false)
public abstract class MixinOvenTakeTool {

    @Inject(
        method = { "onBlockActivated", "func_149727_a" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$takeTool(World world, int x, int y, int z, EntityPlayer player, int side,
        float subX, float subY, float subZ, CallbackInfoReturnable<Boolean> result) {
        if (side != ForgeDirection.UP.ordinal() || !player.isSneaking() || player.getHeldItem() != null) return;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof IInventory)) return;
        IInventory oven = (IInventory) tile;
        int slot = KitchenHands.ovenToolSlot(world.getBlockMetadata(x, y, z), subX, subZ);
        ItemStack tool = oven.getStackInSlot(slot);
        if (tool == null) return;
        // Both sides take it, as the oven places cookware on both.
        oven.setInventorySlotContents(slot, null);
        player.inventory.setInventorySlotContents(player.inventory.currentItem, tool);
        result.setReturnValue(true);
    }
}
