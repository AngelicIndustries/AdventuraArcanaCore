package industries.angelic.adventuraarcanacore.compat.realistictorches;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * A lit bone torch of a flame Netherlicious has none of (the Aether's): Netherlicious' bone torch with that flame's
 * colours (TorchPaint), its light and its flame's particles, from the wooden torch of the flame.
 */
public class BlockBoneTorch extends BlockTorch {

    private final Torches.Kind kind;

    public BlockBoneTorch(Torches.Kind kind) {
        this.kind = kind;
        setBlockName(AdventuraArcanaCore.MODID + "." + kind.id());
        setHardness(0);
        setLightLevel(0.9375F);
        setStepSound(soundTypeWood);
        setTickRandomly(false);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.getBlock(x, y, z) == this) TorchClock.placed(world, x, y, z, this);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        return TorchLighting.light(world, x, y, z, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = PaintedSprite
            .register(register, AdventuraArcanaCore.MODID + ":" + kind.id(), source -> TorchPaint.lit(source, kind));
    }

    /** The wooden torch's smoke and flame, which the Aether's torches put where vanilla's would. */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        Block wood = Torches.lit(Torches.Material.WOOD, kind.flame);
        if (wood != null) wood.randomDisplayTick(world, x, y, z, random);
        else super.randomDisplayTick(world, x, y, z, random);
    }
}
