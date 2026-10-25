package industries.angelic.adventuraarcanacore.food;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockVine;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * A wild fruiting vine on tree trunks (proposal v3, "vine"): grapes and kiwis. Metadata is the vine's faces, as for
 * vanilla's vine; picking the fruit turns it into its bare twin, which fruits again in time. It never spreads.
 */
public final class BlockFruitVine extends BlockVine {

    private final String crop;
    private final boolean fruiting;
    BlockFruitVine twin;
    @SideOnly(Side.CLIENT)
    private IIcon icon;

    BlockFruitVine(String crop, boolean fruiting) {
        this.crop = crop;
        this.fruiting = fruiting;
        setHardness(0.2F);
        setStepSound(soundTypeGrass);
        setBlockName("adventuraarcanacore." + crop + "_vine" + (fruiting ? "" : "_bare"));
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote && !fruiting && random.nextInt(20) == 0 && world.getBlockLightValue(x, y, z) >= 9)
            world.setBlock(x, y, z, twin, world.getBlockMetadata(x, y, z), 3);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!fruiting) return false;
        if (!world.isRemote) {
            for (ItemStack drop : getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0))
                dropBlockAsItem(world, x, y, z, drop);
            world.setBlock(x, y, z, twin, world.getBlockMetadata(x, y, z), 3);
        }
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        if (!fruiting) return drops;
        Item produce = PamFood.produce(crop), seed = PamFood.seed(crop);
        if ("grape".equals(crop) && GrowthcraftGrapes.active()) {
            produce = GrowthcraftGrapes.grapes();
            seed = GrowthcraftGrapes.seeds();
        }
        if (produce != null) drops.add(new ItemStack(produce, 1 + world.rand.nextInt(2)));
        if (seed != null && world.rand.nextInt(3) == 0) drops.add(new ItemStack(seed));
        return drops;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(fruiting ? twin : this, 1, 0));
        return drops;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor() {
        return 0xFFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int metadata) {
        return 0xFFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return 0xFFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        String fruit = fruiting ? crop : null;
        icon = PaintedSprite.register(
            register,
            "adventuraarcanacore:food/" + crop + "_vine" + (fruiting ? "" : "_bare"),
            source -> FoodPaint.vine(source, fruit));
    }
}
