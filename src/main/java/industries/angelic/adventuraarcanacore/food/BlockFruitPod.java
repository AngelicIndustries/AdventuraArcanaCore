package industries.angelic.adventuraarcanacore.food;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * The hanging crop of a Pam-like tree for HarvestCraft's tree crops (proposal v3, "tree"): coffee cherries, curry and
 * bay berries, tea flowers. It hangs under leaves and ripens in three stages like HarvestCraft's own fruit; a ripe
 * pod picked by hand starts again. Drawn from HarvestCraft's fruit textures in its own colour.
 */
public final class BlockFruitPod extends Block {

    private final String crop;
    private final String texture;
    private final int colour;
    @SideOnly(Side.CLIENT)
    private IIcon[] stages;

    BlockFruitPod(String crop, String texture, int colour) {
        super(Material.plants);
        this.crop = crop;
        this.texture = texture;
        this.colour = colour;
        setTickRandomly(true);
        setHardness(0.2F);
        setStepSound(soundTypeGrass);
        setBlockName("adventuraarcanacore." + crop + "_pod");
        setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 1.0F, 0.75F);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.isAirBlock(x, y, z) && canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return world.getBlock(x, y + 1, z)
            .isLeaves(world, x, y + 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
        if (!canBlockStay(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 2 && random.nextInt(30) == 0) world.setBlockMetadataWithNotify(x, y, z, meta + 1, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.getBlockMetadata(x, y, z) != 2) return false;
        if (!world.isRemote) {
            for (ItemStack drop : getDrops(world, x, y, z, 2, 0)) dropBlockAsItem(world, x, y, z, drop);
            world.setBlockMetadataWithNotify(x, y, z, 0, 3);
        }
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        if (metadata < 2) return drops;
        Item produce = PamFood.produce(crop), seed = PamFood.seed(crop);
        if (produce != null) drops.add(new ItemStack(produce, 1 + world.rand.nextInt(2)));
        if (seed != null && world.rand.nextInt(4) == 0) drops.add(new ItemStack(seed));
        return drops;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
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
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return stages[Math.min(2, metadata)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        stages = new IIcon[3];
        for (int stage = 0; stage < 3; stage++) {
            int s = stage;
            stages[stage] = PaintedSprite.register(
                register,
                "adventuraarcanacore:food/" + crop + "_pod_" + stage,
                source -> FoodPaint.pod(source, texture, s, colour));
        }
    }
}
