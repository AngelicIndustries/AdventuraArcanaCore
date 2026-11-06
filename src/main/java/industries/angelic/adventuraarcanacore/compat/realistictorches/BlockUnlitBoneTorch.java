package industries.angelic.adventuraarcanacore.compat.realistictorches;

import java.util.Random;

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
 * What Netherlicious' bone torches burn down to: a bone torch with a charred tip, painted from its texture and the tip
 * of Realistic Torches' unlit torch (TorchPaint). Any lighter lights it into its flame's bone torch.
 */
public class BlockUnlitBoneTorch extends BlockTorch {

    public BlockUnlitBoneTorch() {
        setBlockName(AdventuraArcanaCore.MODID + ".unlit_bone_torch");
        setHardness(0);
        setStepSound(soundTypeWood);
        setTickRandomly(false);
        setCreativeTab(CreativeTabs.tabDecorations);
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
            .register(register, AdventuraArcanaCore.MODID + ":unlit_bone_torch", TorchPaint::unlitBone);
    }

    /** No flame, no smoke. */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {}
}
