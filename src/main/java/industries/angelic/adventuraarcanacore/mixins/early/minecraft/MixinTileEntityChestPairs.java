package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.adventuraarcanacore.compat.chests.ChestPairs;

/**
 * Manual chests (placed while sneaking) on AdjaChest's pairing: one keeps only the pair it was placed with and takes no
 * other, and no neighbor takes it. The pair direction is AdjaChest's. Applied after AdjaChest (higher priority) to
 * reach the methods it adds.
 */
@Mixin(value = TileEntityChest.class, priority = 1100)
public abstract class MixinTileEntityChestPairs extends TileEntity implements ChestPairs.Pairable {

    @Shadow
    public boolean adjacentChestChecked;
    @Shadow
    public TileEntityChest adjacentChestZNeg;
    @Shadow
    public TileEntityChest adjacentChestXPos;
    @Shadow
    public TileEntityChest adjacentChestXNeg;
    @Shadow
    public TileEntityChest adjacentChestZPos;
    @Shadow(remap = false)
    public ForgeDirection adjachest$doubleDirection;
    @Shadow(remap = false)
    boolean adjachest$finishedLoading;

    @Unique
    private boolean adventuraarcanacore$manual;

    @Shadow(remap = false)
    protected abstract boolean aValidDouble(int x, int y, int z);

    @Shadow
    public abstract void func_145976_a(String name);

    /** A manual chest without its pair stays single, instead of AdjaChest finding it one. */
    @Inject(method = "adjachest$checkForAdjacentChests", at = @At("HEAD"), cancellable = true, remap = false)
    private void adventuraarcanacore$keepManual(CallbackInfo ci) {
        if (!adventuraarcanacore$manual || !hasWorldObj() || adjacentChestChecked || ChestPairs.partner(this) != null)
            return;
        adjacentChestChecked = true;
        adjacentChestZNeg = adjacentChestXPos = adjacentChestXNeg = adjacentChestZPos = null;
        // A client keeps the direction it was sent: the partner's own update may still be on its way.
        if (!worldObj.isRemote && adjachest$doubleDirection != ForgeDirection.UNKNOWN) {
            adjachest$doubleDirection = ForgeDirection.UNKNOWN;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        ci.cancel();
    }

    /** AdjaChest skips a manual neighbor unless it points back here. */
    @WrapOperation(
        method = "adjachest$checkForAdjacentChests",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/tileentity/TileEntityChest;aValidDouble(III)Z",
            remap = false),
        remap = false)
    private boolean adventuraarcanacore$skipManual(TileEntityChest self, int x, int y, int z,
        Operation<Boolean> original) {
        if (!original.call(self, x, y, z)) return false;
        TileEntity other = worldObj.getTileEntity(x, y, z);
        if (!(other instanceof ChestPairs.Pairable) || !((ChestPairs.Pairable) other).adventuraarcanacore$manual())
            return true;
        ForgeDirection back = ((ChestPairs.Pairable) other).adventuraarcanacore$pairedTo();
        return x + back.offsetX == xCoord && z + back.offsetZ == zCoord && back != ForgeDirection.UNKNOWN;
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void adventuraarcanacore$saveManual(NBTTagCompound tag, CallbackInfo ci) {
        if (adventuraarcanacore$manual) tag.setBoolean(ChestPairs.MANUAL_TAG, true);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void adventuraarcanacore$loadManual(NBTTagCompound tag, CallbackInfo ci) {
        adventuraarcanacore$manual = tag.getBoolean(ChestPairs.MANUAL_TAG);
    }

    // AdjaChest's update packet, which carries its pair direction.
    @Inject(method = { "getDescriptionPacket", "func_145844_m" }, at = @At("RETURN"), remap = false)
    private void adventuraarcanacore$sendManual(CallbackInfoReturnable<Packet> cir) {
        if (adventuraarcanacore$manual && cir.getReturnValue() instanceof S35PacketUpdateTileEntity)
            ((S35PacketUpdateTileEntity) cir.getReturnValue()).func_148857_g()
                .setBoolean(ChestPairs.MANUAL_TAG, true);
    }

    @Inject(method = "onDataPacket", at = @At("TAIL"), remap = false)
    private void adventuraarcanacore$receiveManual(NetworkManager net, S35PacketUpdateTileEntity packet,
        CallbackInfo ci) {
        adventuraarcanacore$manual = packet.func_148857_g()
            .getBoolean(ChestPairs.MANUAL_TAG);
        ChestPairs.received(this);
    }

    @Override
    public ForgeDirection adventuraarcanacore$pairedTo() {
        return adjachest$doubleDirection;
    }

    @Override
    public void adventuraarcanacore$pairTo(ForgeDirection direction) {
        adjachest$doubleDirection = direction;
    }

    @Override
    public boolean adventuraarcanacore$manual() {
        return adventuraarcanacore$manual;
    }

    @Override
    public void adventuraarcanacore$setManual(boolean manual) {
        adventuraarcanacore$manual = manual;
    }

    @Override
    public boolean adventuraarcanacore$pairsWith(int x, int y, int z) {
        return aValidDouble(x, y, z);
    }

    @Override
    public void adventuraarcanacore$recheck() {
        adjacentChestChecked = false;
    }

    @Override
    public void adventuraarcanacore$placed(ItemStack stack) {
        if (stack.hasDisplayName()) func_145976_a(stack.getDisplayName());
        adjachest$finishedLoading = true;
    }
}
