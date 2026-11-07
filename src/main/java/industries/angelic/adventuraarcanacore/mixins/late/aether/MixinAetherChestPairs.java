package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.chests.ChestPairs;

/**
 * The Aether's chests (Skyroot, Elysian, breakable treasure) each pair with the one neighbor AAC chose, as AdjaChest
 * pairs vanilla chests, instead of with every neighbor of their kind. They keep and send their pair; mimics pair with
 * nothing.
 */
@Pseudo
@Mixin(
    targets = { "com.gildedgames.the_aether.tileentity.TileEntitySkyrootChest",
        "com.gildedgames.the_aether.tileentity.TileEntityElysianChest",
        "com.gildedgames.the_aether.tileentity.TileEntityTreasureChestBreakable" },
    remap = false)
public abstract class MixinAetherChestPairs extends TileEntity implements ChestPairs.Pairable {

    @Shadow(remap = false)
    public boolean adjacentChestChecked;

    @Unique
    private ForgeDirection adventuraarcanacore$pairedTo = ForgeDirection.UNKNOWN;
    @Unique
    private boolean adventuraarcanacore$manual;

    @Shadow(remap = false)
    public abstract void setChestGuiName(String name);

    @Inject(method = "checkForAdjacentChests", at = @At("HEAD"))
    private void adventuraarcanacore$pair(CallbackInfo ci) {
        if (!adjacentChestChecked) ChestPairs.resolve(this);
    }

    /** Its "same chest there" check, which made it join every neighbor: only its partner counts. */
    @Inject(method = "func_94044_a", at = @At("HEAD"), cancellable = true)
    private void adventuraarcanacore$onlyPartner(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ChestPairs.isPartnerAt(this, x, y, z));
    }

    @Inject(method = { "writeToNBT", "func_145841_b" }, at = @At("TAIL"))
    private void adventuraarcanacore$save(NBTTagCompound tag, CallbackInfo ci) {
        adventuraarcanacore$write(tag);
    }

    @Inject(method = { "readFromNBT", "func_145839_a" }, at = @At("TAIL"))
    private void adventuraarcanacore$load(NBTTagCompound tag, CallbackInfo ci) {
        adventuraarcanacore$read(tag);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        adventuraarcanacore$write(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        adventuraarcanacore$read(packet.func_148857_g());
        ChestPairs.received(this);
    }

    @Unique
    private void adventuraarcanacore$write(NBTTagCompound tag) {
        if (adventuraarcanacore$pairedTo != ForgeDirection.UNKNOWN)
            tag.setByte(ChestPairs.PAIR_TAG, (byte) adventuraarcanacore$pairedTo.ordinal());
        if (adventuraarcanacore$manual) tag.setBoolean(ChestPairs.MANUAL_TAG, true);
    }

    @Unique
    private void adventuraarcanacore$read(NBTTagCompound tag) {
        ForgeDirection direction = tag.hasKey(ChestPairs.PAIR_TAG)
            ? ForgeDirection.getOrientation(tag.getByte(ChestPairs.PAIR_TAG))
            : ForgeDirection.UNKNOWN;
        adventuraarcanacore$pairedTo = direction.offsetY == 0 ? direction : ForgeDirection.UNKNOWN;
        adventuraarcanacore$manual = tag.getBoolean(ChestPairs.MANUAL_TAG);
    }

    @Override
    public ForgeDirection adventuraarcanacore$pairedTo() {
        return adventuraarcanacore$pairedTo;
    }

    @Override
    public void adventuraarcanacore$pairTo(ForgeDirection direction) {
        adventuraarcanacore$pairedTo = direction;
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
        return hasWorldObj() && !adventuraarcanacore$mimic(this)
            && worldObj.getBlock(x, y, z) == worldObj.getBlock(xCoord, yCoord, zCoord)
            && !adventuraarcanacore$mimic(worldObj.getTileEntity(x, y, z));
    }

    @Override
    public void adventuraarcanacore$recheck() {
        adjacentChestChecked = false;
    }

    @Override
    public void adventuraarcanacore$placed(ItemStack stack) {
        if (stack.hasDisplayName()) setChestGuiName(stack.getDisplayName());
    }

    @Unique
    private static boolean adventuraarcanacore$mimic(TileEntity tile) {
        return tile == null || tile.getClass()
            .getSimpleName()
            .endsWith("Mimic");
    }
}
