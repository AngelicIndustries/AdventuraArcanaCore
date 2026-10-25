package industries.angelic.adventuraarcanacore.compartment;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

public class BlockCompartment extends BlockContainer {

    public BlockCompartment() {
        super(Material.iron);
        setBlockName("adventuraarcanacore.compartment");
        setBlockTextureName("planks_oak");
        setCreativeTab(CreativeTabs.tabDecorations);
        setHardness(1.5F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileCompartment(CompartmentTier.fromMetadata(metadata));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public int damageDropped(int metadata) {
        return CompartmentTier.fromMetadata(metadata)
            .ordinal();
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> result) {
        for (CompartmentTier tier : CompartmentTier.values()) result.add(new ItemStack(item, 1, tier.ordinal()));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (player.isSneaking()) return false;
        if (!world.isRemote) player.openGui(AdventuraArcanaCore.instance, Compartments.GUI_ID, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileCompartment inventory) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlotOnClosing(i);
                if (stack == null) continue;
                EntityItem item = new EntityItem(
                    world,
                    x + 0.1 + world.rand.nextFloat() * 0.8,
                    y + 0.1 + world.rand.nextFloat() * 0.8,
                    z + 0.1 + world.rand.nextFloat() * 0.8,
                    stack);
                item.motionX = world.rand.nextGaussian() * 0.05;
                item.motionY = world.rand.nextGaussian() * 0.05 + 0.2;
                item.motionZ = world.rand.nextGaussian() * 0.05;
                world.spawnEntityInWorld(item);
            }
            world.func_147453_f(x, y, z, block);
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileCompartment inventory ? Container.calcRedstoneFromInventory(inventory) : 0;
    }
}
