package industries.angelic.adventuraarcanacore.worktable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

public class BlockWorktable extends BlockContainer {

    public BlockWorktable() {
        super(Material.wood);
        setBlockName("adventuraarcanacore.worktable");
        setBlockTextureName("planks_oak");
        setCreativeTab(CreativeTabs.tabDecorations);
        setHardness(2.5F);
        setStepSound(soundTypeWood);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileWorktable();
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] faces;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        faces = new IIcon[5];
        for (int i = 0; i < faces.length; i++)
            faces[i] = register.registerIcon("adventuraarcanacore:worktable/worktable." + i);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        if (side < 2) return faces[side];
        int front = metadata >= 2 && metadata <= 5 ? metadata : 3;
        return faces[side == front ? 3 : side == (front ^ 1) ? 2 : 4];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int direction = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, new int[] { 2, 5, 3, 4 }[direction], 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (player.isSneaking()) return false;
        if (!world.isRemote) player.openGui(AdventuraArcanaCore.instance, Worktables.GUI_ID, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileWorktable inventory) {
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
        return tile instanceof TileWorktable inventory ? Container.calcRedstoneFromInventory(inventory) : 0;
    }
}
