package industries.angelic.adventuraarcanacore.food;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * HarvestCraft's shrub crops growing wild (proposal v3, "bush"): bayberry (candleberry), wild cotton, cranberry and
 * raspberry, drawn as PMP's berry bush in their colours. Right-click picks the fruit and leaves the bush bare until it
 * fruits again. Metadata: species (0 to 3), plus 4 when picked.
 */
public final class BlockWildBush extends BlockBush {

    static final String[] SPECIES = { "candleberry", "cotton", "cranberry", "raspberry" };
    private static final int[] COLOURS = { 0xD8DCD4, 0xF2F0E8, 0xA5122A, 0xD6274D };
    static final int PICKED = 4;

    @SideOnly(Side.CLIENT)
    private IIcon[] ripe;
    @SideOnly(Side.CLIENT)
    private IIcon bare;

    BlockWildBush() {
        super(Material.plants);
        setTickRandomly(true);
        setHardness(0.2F);
        setStepSound(soundTypeGrass);
        setBlockName("adventuraarcanacore.wild_bush");
        float f = 0.4F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.85F, 0.5F + f);
    }

    static int species(String crop) {
        for (int i = 0; i < SPECIES.length; i++) if (SPECIES[i].equals(crop)) return i;
        return -1;
    }

    @Override
    protected boolean canPlaceBlockOn(Block ground) {
        return WildCrops.naturalGround(ground);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.getBlock(x, y, z) != this) return;
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & PICKED) != 0 && world.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(12) == 0)
            world.setBlockMetadataWithNotify(x, y, z, meta & 3, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & PICKED) != 0) return false;
        if (!world.isRemote) {
            String crop = SPECIES[meta & 3];
            Item produce = PamFood.produce(crop), seed = PamFood.seed(crop);
            if (produce != null) dropBlockAsItem(world, x, y, z, new ItemStack(produce, 1 + world.rand.nextInt(3)));
            if (seed != null && world.rand.nextInt(3) == 0) dropBlockAsItem(world, x, y, z, new ItemStack(seed));
            world.setBlockMetadataWithNotify(x, y, z, meta | PICKED, 3);
        }
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        String crop = SPECIES[metadata & 3];
        Item produce = PamFood.produce(crop), seed = PamFood.seed(crop);
        if ((metadata & PICKED) == 0 && produce != null) drops.add(new ItemStack(produce, 1 + world.rand.nextInt(2)));
        if (seed != null && world.rand.nextInt((metadata & PICKED) == 0 ? 2 : 4) == 0) drops.add(new ItemStack(seed));
        return drops;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata & 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < SPECIES.length; i++) list.add(new ItemStack(item, 1, i));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return (metadata & PICKED) != 0 ? bare : ripe[metadata & 3];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        ripe = new IIcon[SPECIES.length];
        for (int i = 0; i < SPECIES.length; i++) {
            String crop = SPECIES[i];
            int colour = COLOURS[i];
            ripe[i] = PaintedSprite.register(
                register,
                "adventuraarcanacore:food/wild_bush_" + crop,
                source -> FoodPaint.bush(source, crop, colour));
        }
        bare = PaintedSprite.register(
            register,
            "adventuraarcanacore:food/wild_bush_bare",
            source -> FoodPaint.bareBush(source, "raspberry"));
    }
}
