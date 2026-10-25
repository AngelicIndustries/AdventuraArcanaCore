package industries.angelic.adventuraarcanacore.compartment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** Adapted from Binnie's CompartmentTab, LGPL-3.0. */
public final class CompartmentTab {

    public static final int MAX_NAME_LENGTH = 32;
    private String name = "";
    private ItemStack icon = new ItemStack(Items.paper);
    private int color = 15;

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon.copy();
    }

    public int getColor() {
        return color;
    }

    public void setName(String name) {
        String clean = name == null ? "" : name.replaceAll("[\\p{Cntrl}§]", "");
        this.name = clean.substring(0, Math.min(clean.length(), MAX_NAME_LENGTH));
    }

    public void setIcon(ItemStack stack) {
        icon = stack == null ? new ItemStack(Items.paper) : stack.copy();
        icon.stackSize = 1;
    }

    public void setColor(int color) {
        this.color = color >= 0 && color < 16 ? color : 15;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("name", name);
        nbt.setByte("color", (byte) color);
        NBTTagCompound item = new NBTTagCompound();
        icon.writeToNBT(item);
        nbt.setTag("icon", item);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        setName(nbt.getString("name"));
        setColor(nbt.hasKey("color") ? nbt.getByte("color") : 15);
        setIcon(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("icon")));
    }
}
