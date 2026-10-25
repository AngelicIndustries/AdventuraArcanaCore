package industries.angelic.adventuraarcanacore.compartment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileCompartment extends TileEntity implements IInventory {

    private CompartmentTier tier;
    private ItemStack[] items;
    private CompartmentTab[] tabs;

    public TileCompartment() {
        this(CompartmentTier.WOOD);
    }

    public TileCompartment(CompartmentTier tier) {
        initialize(tier);
    }

    private void initialize(CompartmentTier tier) {
        this.tier = tier;
        items = new ItemStack[tier.capacity()];
        tabs = new CompartmentTab[tier.tabs];
        for (int i = 0; i < tabs.length; i++) tabs[i] = new CompartmentTab();
    }

    public CompartmentTier getTier() {
        return tier;
    }

    public CompartmentTab getTab(int index) {
        return tabs[index];
    }

    public boolean isValidTab(int tab) {
        return tab >= 0 && tab < tabs.length;
    }

    public void tabChanged() {
        markDirty();
        if (worldObj != null && !worldObj.isRemote) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < items.length ? items[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null || amount <= 0) return null;
        ItemStack result;
        if (stack.stackSize <= amount) {
            result = stack;
            items[slot] = null;
        } else {
            result = stack.splitStack(amount);
        }
        markDirty();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return decrStackSize(slot, Integer.MAX_VALUE);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.length) return;
        items[slot] = stack;
        if (stack != null)
            stack.stackSize = Math.min(stack.stackSize, Math.min(getInventoryStackLimit(), stack.getMaxStackSize()));
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "tile.adventuraarcanacore.compartment." + tier.key + ".name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj != null && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
            && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot >= 0 && slot < items.length;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        writeTabs(nbt);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) continue;
            NBTTagCompound item = new NBTTagCompound();
            item.setInteger("Slot", i);
            items[i].writeToNBT(item);
            list.appendTag(item);
        }
        nbt.setTag("Items", list);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        initialize(CompartmentTier.fromMetadata(nbt.getInteger("Tier")));
        readTabs(nbt);
        NBTTagList list = nbt.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getInteger("Slot");
            if (slot >= 0 && slot < items.length) items[slot] = ItemStack.loadItemStackFromNBT(item);
        }
    }

    private void writeTabs(NBTTagCompound nbt) {
        nbt.setInteger("Tier", tier.ordinal());
        NBTTagList list = new NBTTagList();
        for (CompartmentTab tab : tabs) {
            NBTTagCompound data = new NBTTagCompound();
            tab.writeToNBT(data);
            list.appendTag(data);
        }
        nbt.setTag("Tabs", list);
    }

    private void readTabs(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("Tabs", 10);
        for (int i = 0; i < Math.min(tabs.length, list.tagCount()); i++) tabs[i].readFromNBT(list.getCompoundTagAt(i));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeTabs(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        NBTTagCompound nbt = packet.func_148857_g();
        CompartmentTier updated = CompartmentTier.fromMetadata(nbt.getInteger("Tier"));
        if (updated != tier) initialize(updated);
        readTabs(nbt);
    }
}
