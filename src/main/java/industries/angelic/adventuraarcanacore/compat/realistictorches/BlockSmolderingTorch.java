package industries.angelic.adventuraarcanacore.compat.realistictorches;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * A Netherlicious torch smoldering out, like Realistic Torches' smoldering torch: dimmer, smoking, its flame now and
 * then. Painted from the lit torch's texture with a faded flame (TorchPaint). It drops its unlit torch.
 */
public class BlockSmolderingTorch extends BlockTorch {

    private final Torches.Kind kind;

    public BlockSmolderingTorch(Torches.Kind kind) {
        this.kind = kind;
        setBlockName(AdventuraArcanaCore.MODID + ".smoldering_" + kind.id());
        setHardness(0);
        setStepSound(soundTypeWood);
        setTickRandomly(false);
        setCreativeTab(null);
    }

    /** About two thirds of the lit torch's light, as Realistic Torches' smoldering torch has. */
    void lightLike(Block lit) {
        setLightLevel(lit.getLightValue() * 0.65F / 15);
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
    public Item getItemDropped(int meta, Random random, int fortune) {
        Block unlit = kind.unlit();
        return unlit == null ? null : Item.getItemFromBlock(unlit);
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        return getItemDropped(0, world.rand, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = PaintedSprite.register(
            register,
            AdventuraArcanaCore.MODID + ":smoldering_" + kind.id(),
            source -> TorchPaint.smolder(source, kind));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        // Where the vanilla torch puts its flame, by which way it faces.
        int meta = world.getBlockMetadata(x, y, z);
        double px = x + 0.5, py = y + 0.7, pz = z + 0.5;
        if (meta >= 1 && meta <= 4) {
            py += 0.22;
            if (meta == 1) px -= 0.27;
            else if (meta == 2) px += 0.27;
            else if (meta == 3) pz -= 0.27;
            else pz += 0.27;
        }
        world.spawnParticle("smoke", px, py, pz, 0, 0, 0);
        if (random.nextInt(4) == 0 && kind.lit() != null) kind.lit()
            .randomDisplayTick(world, x, y, z, random);
    }
}
