package industries.angelic.adventuraarcanacore.compartment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import cpw.mods.fml.common.Loader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@RunWith(ForgeTestRunner.class)
public class CompartmentTest {

    @BeforeClass
    public static void bootstrap() {
        Loader.injectData("7", "99", "40", "1614", "1.7.10", "9.05", new File("."), Collections.emptyList());
        Bootstrap.func_151354_b();
        TileEntity.addMapping(TileCompartment.class, "aac_test_compartment");
    }

    @Test
    public void everyTierPreservesAllSlotsAndTabMetadata() {
        int[] expected = { 100, 150, 200, 200, 300, 400 };
        for (CompartmentTier tier : CompartmentTier.values()) {
            TileCompartment original = new TileCompartment(tier);
            assertEquals(expected[tier.ordinal()], original.getSizeInventory());
            for (int i = 0; i < original.getSizeInventory(); i++) {
                ItemStack stack = new ItemStack(Items.paper, i % 64 + 1);
                stack.setStackDisplayName("Slot " + i);
                original.setInventorySlotContents(i, stack);
            }
            int lastTab = tier.tabs - 1;
            original.getTab(lastTab)
                .setName("Last tab");
            original.getTab(lastTab)
                .setColor(3);
            ItemStack icon = new ItemStack(Items.diamond, 17);
            original.getTab(lastTab)
                .setIcon(icon);
            assertEquals(17, icon.stackSize);

            NBTTagCompound nbt = new NBTTagCompound();
            original.writeToNBT(nbt);
            TileCompartment restored = new TileCompartment();
            restored.readFromNBT(nbt);
            assertEquals(tier, restored.getTier());
            for (int i = 0; i < restored.getSizeInventory(); i++) {
                assertTrue(
                    "Slot " + i,
                    ItemStack.areItemStacksEqual(original.getStackInSlot(i), restored.getStackInSlot(i)));
            }
            assertEquals(
                "Last tab",
                restored.getTab(lastTab)
                    .getName());
            assertEquals(
                3,
                restored.getTab(lastTab)
                    .getColor());
            assertEquals(
                Items.diamond,
                restored.getTab(lastTab)
                    .getIcon()
                    .getItem());
            assertEquals(
                1,
                restored.getTab(lastTab)
                    .getIcon().stackSize);
            assertEquals(
                0,
                restored
                    .getDescriptionPacket() instanceof net.minecraft.network.play.server.S35PacketUpdateTileEntity packet
                        ? packet.func_148857_g()
                            .getTagList("Items", 10)
                            .tagCount()
                        : -1);
        }
    }

    @Test
    public void malformedSaveCannotIndexOutsideInventory() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Tier", 1000);
        NBTTagList items = new NBTTagList();
        for (int index : new int[] { -1, 400, Integer.MAX_VALUE }) {
            NBTTagCompound entry = new NBTTagCompound();
            new ItemStack(Items.diamond).writeToNBT(entry);
            entry.setInteger("Slot", index);
            items.appendTag(entry);
        }
        nbt.setTag("Items", items);
        TileCompartment tile = new TileCompartment();
        tile.readFromNBT(nbt);
        assertEquals(CompartmentTier.WOOD, tile.getTier());
        assertEquals(0, count(tile));
        CompartmentTab tab = new CompartmentTab();
        nbt.setByte("color", (byte) -100);
        tab.readFromNBT(nbt);
        assertEquals(15, tab.getColor());
        assertEquals(
            Items.paper,
            tab.getIcon()
                .getItem());
        tab.setName("Tools\n\t\u00a7");
        assertEquals("Tools", tab.getName());
        tab.setName(String.join("", Collections.nCopies(40, "x")));
        assertEquals(
            32,
            tab.getName()
                .length());
    }

    @Test
    public void extractionAndBreakingDrainEachSlotOnce() {
        TileCompartment tile = new TileCompartment(CompartmentTier.DIAMOND);
        tile.setInventorySlotContents(399, new ItemStack(Items.diamond, 64));
        assertEquals(7, tile.decrStackSize(399, 7).stackSize);
        assertEquals(57, tile.getStackInSlot(399).stackSize);
        assertNull(tile.decrStackSize(399, 0));
        assertEquals(57, tile.getStackInSlotOnClosing(399).stackSize);
        assertNull(tile.getStackInSlotOnClosing(399));
        assertEquals(0, count(tile));
    }

    @Test
    public void shiftClickUsesOnlySelectedTabAndPreservesCounts() {
        TileCompartment tile = usableTile();
        InventoryPlayer inventory = new InventoryPlayer(null);
        ContainerCompartment first = new ContainerCompartment(inventory, tile);
        ContainerCompartment second = new ContainerCompartment(new InventoryPlayer(null), tile);
        assertTrue(first.enchantItem(null, 7));
        assertEquals(7, first.getCurrentTab());
        assertEquals(0, second.getCurrentTab());
        assertFalse(first.enchantItem(null, 127));
        inventory.setInventorySlotContents(9, new ItemStack(Items.diamond, 64));
        assertNotNull(first.transferStackInSlot(null, tile.getSizeInventory()));
        assertEquals(64, tile.getStackInSlot(350).stackSize);
        assertNull(tile.getStackInSlot(0));
        assertNull(inventory.getStackInSlot(9));
        assertNull(second.transferStackInSlot(null, 350));
        assertEquals(64, count(tile));
        assertNotNull(first.transferStackInSlot(null, 350));
        assertEquals(0, count(tile));
        assertEquals(64, count(inventory));
    }

    @Test
    public void fullTabDoesNotSpillIntoAnotherTab() {
        TileCompartment tile = usableTile();
        InventoryPlayer inventory = new InventoryPlayer(null);
        ContainerCompartment container = new ContainerCompartment(inventory, tile);
        for (int i = 0; i < 50; i++) tile.setInventorySlotContents(i, new ItemStack(Items.paper, 64));
        inventory.setInventorySlotContents(9, new ItemStack(Items.diamond, 64));
        assertNull(container.transferStackInSlot(null, 400));
        assertEquals(64, inventory.getStackInSlot(9).stackSize);
        assertNull(tile.getStackInSlot(50));
        tile.setInventorySlotContents(49, new ItemStack(Items.paper, 60));
        inventory.setInventorySlotContents(9, new ItemStack(Items.paper, 10));
        container.transferStackInSlot(null, 400);
        assertEquals(64, tile.getStackInSlot(49).stackSize);
        assertEquals(6, inventory.getStackInSlot(9).stackSize);
    }

    @Test
    public void searchFindsOtherPagesFiltersAndSortsWithoutMovingItems() {
        TileCompartment tile = new TileCompartment(CompartmentTier.DIAMOND);
        tile.setInventorySlotContents(0, named(new ItemStack(Items.diamond), "Zebra"));
        tile.setInventorySlotContents(399, named(new ItemStack(Blocks.stone), "alpha"));
        tile.setInventorySlotContents(256, named(new ItemStack(Items.paper), "ALPHABET"));
        assertEquals(Arrays.asList(256, 399), CompartmentSearch.findSlots(tile, "ALPHA", true, true, false));
        assertEquals(Arrays.asList(399, 256, 0), CompartmentSearch.findSlots(tile, "", true, true, true));
        assertEquals(Arrays.asList(0, 256), CompartmentSearch.findSlots(tile, "", true, false, false));
        assertEquals(Collections.singletonList(399), CompartmentSearch.findSlots(tile, "", false, true, false));
        assertTrue(
            CompartmentSearch.findSlots(tile, "", false, false, false)
                .isEmpty());
        assertEquals(
            "Zebra",
            tile.getStackInSlot(0)
                .getDisplayName());
        assertEquals(3, count(tile));
    }

    @Test
    public void renamePacketRoundTripsUnicodeAndBoundsInput() {
        ByteBuf encoded = Unpooled.buffer();
        ByteBuf roundTrip = Unpooled.buffer();
        try {
            new CompartmentNetwork.Rename(27, 7, "Matériaux 木材").toBytes(encoded);
            CompartmentNetwork.Rename decoded = new CompartmentNetwork.Rename();
            decoded.fromBytes(encoded.duplicate());
            decoded.toBytes(roundTrip);
            assertEquals(encoded, roundTrip);
        } finally {
            encoded.release();
            roundTrip.release();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void renamePacketRejectsOversizedNames() {
        ByteBuf encoded = Unpooled.buffer();
        try {
            encoded.writeInt(1)
                .writeByte(0)
                .writeByte(255)
                .writeZero(255);
            new CompartmentNetwork.Rename().fromBytes(encoded);
        } finally {
            encoded.release();
        }
    }

    private static TileCompartment usableTile() {
        return new TileCompartment(CompartmentTier.DIAMOND) {

            @Override
            public boolean isUseableByPlayer(EntityPlayer player) {
                return true;
            }
        };
    }

    private static ItemStack named(ItemStack stack, String name) {
        stack.setStackDisplayName(name);
        return stack;
    }

    private static int count(net.minecraft.inventory.IInventory inventory) {
        int count = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) count += stack.stackSize;
        }
        return count;
    }
}
