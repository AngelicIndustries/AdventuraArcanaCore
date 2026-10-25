package industries.angelic.adventuraarcanacore.food;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * A bamboo shoot of one species (review, 2026-10-25), between HarvestCraft's shoot crop and Growthcraft's shoot: a
 * short plant that gives its shoot item when broken (cooked as HarvestCraft's cropBambooshoot, or planted again) for
 * as long as it is a shoot, about half an hour, or when picked by hand fully grown, after which it starts again; left
 * past its last stage it becomes its bamboo, a PMP stalk that PMP grows on, or a Madake tree. Eight stages, drawn as
 * HarvestCraft's four in the colour of the species' culm; only the last, the one picked by hand, looks fully grown.
 */
public final class BlockBambooShoot extends BlockBush implements IGrowable {

    static final int MATURE = 7;
    /** One random tick in this many grows a stage (about 4.5 minutes a stage at the default tick speed). */
    private static final int GROWTH = 4;

    final BambooSpecies species;
    @SideOnly(Side.CLIENT)
    private IIcon[] stages;

    BlockBambooShoot(BambooSpecies species) {
        super(Material.plants);
        this.species = species;
        setTickRandomly(true);
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
        setBlockName("adventuraarcanacore.bamboo_shoot_" + species.id);
        setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.getBlock(x, y, z) == this && world.getBlockLightValue(x, y + 1, z) >= 9
            && random.nextInt(GROWTH) == 0) grow(world, x, y, z, random, 1);
    }

    private void grow(World world, int x, int y, int z, Random random, int stages) {
        int metadata = world.getBlockMetadata(x, y, z) + stages;
        if (metadata <= MATURE) world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
        else becomeBamboo(world, x, y, z, random);
    }

    /** Past its last stage: its bamboo where there is room; until then it waits, fully grown. */
    private void becomeBamboo(World world, int x, int y, int z, Random random) {
        if (species == BambooSpecies.MADAKE) {
            if (!MadakeTree.grow(world, random, x, y, z)) world.setBlockMetadataWithNotify(x, y, z, MATURE, 2);
            return;
        }
        Block bamboo = species.bamboo();
        if (bamboo == null || !world.isAirBlock(x, y + 1, z)) {
            world.setBlockMetadataWithNotify(x, y, z, MATURE, 2);
            return;
        }
        // PMP's stalk: metadata 0 along it, 4 for the leafy top; PMP's own tick grows it from there.
        world.setBlock(x, y, z, bamboo, 0, 3);
        world.setBlock(x, y + 1, z, bamboo, 4, 3);
    }

    /**
     * Picked by hand when fully grown, as HarvestCraft's crops are: its shoots drop and it starts again from its first
     * stage. Left alone instead, it turns into its bamboo.
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.getBlockMetadata(x, y, z) != MATURE) return false;
        if (!world.isRemote) {
            dropBlockAsItem(world, x, y, z, MATURE, 0);
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        Item item = BambooShoots.item();
        if (item != null)
            drops.add(new ItemStack(item, metadata >= 5 && world.rand.nextBoolean() ? 2 : 1, species.ordinal()));
        return drops;
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        return BambooShoots.item();
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return species.ordinal();
    }

    /** Can grow (bonemeal). */
    @Override
    public boolean func_149851_a(World world, int x, int y, int z, boolean client) {
        return true;
    }

    /** Bonemeal takes. */
    @Override
    public boolean func_149852_a(World world, Random random, int x, int y, int z) {
        return true;
    }

    /** Bonemeal grows it one or two stages. */
    @Override
    public void func_149853_b(World world, Random random, int x, int y, int z) {
        grow(world, x, y, z, random, 1 + random.nextInt(2));
    }

    @Override
    public int getRenderType() {
        return 6;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return stages[metadata >= MATURE ? 3 : metadata >= 5 ? 2 : metadata >= 2 ? 1 : 0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        stages = new IIcon[4];
        for (int stage = 0; stage < 4; stage++) {
            String texture = "textures/blocks/bambooshootcrop_" + stage + ".png";
            stages[stage] = PaintedSprite.register(
                register,
                "adventuraarcanacore:food/bamboo_shoot_" + species.id + "_" + stage,
                source -> FoodPaint.shoot(source, texture, species.culmTexture()));
        }
    }
}
