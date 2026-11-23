package industries.angelic.adventuraarcanacore.compat.jars;

import java.util.ArrayList;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Biomes O' Plenty's pixie jar, set down: Twilight Forest's firefly jar with the pixie flying inside (TilePixieJar). It
 * has no item of
 * its own; it's placed from, and drops, BOP's jar.
 */
public class BlockPixieJar extends BlockContainer {

    /** The jar's block renderer, set by the client proxy. */
    public static int renderId = -1;

    IIcon side;
    IIcon top;
    public IIcon cork;

    public BlockPixieJar() {
        super(Material.glass);
        setBlockName(AdventuraArcanaCore.MODID + ".pixie_jar");
        setHardness(0.3F);
        setStepSound(soundTypeWood);
        setLightLevel(8 / 15.0F);
        setBlockBounds(0.1875F, 0.0F, 0.1875F, 0.8125F, 1.0F, 0.8125F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TilePixieJar();
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
        return renderId;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        ItemStack jar = PixieJars.filledJar();
        if (jar != null) drops.add(jar);
        return drops;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return PixieJars.filledJar();
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        side = register.registerIcon(AdventuraArcanaCore.MODID + ":jar_side");
        top = register.registerIcon(AdventuraArcanaCore.MODID + ":jar_top");
        cork = register.registerIcon(AdventuraArcanaCore.MODID + ":jar_cork");
    }

    @Override
    public IIcon getIcon(int face, int meta) {
        return face < 2 ? top : side;
    }
}
