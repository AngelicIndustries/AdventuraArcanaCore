package industries.angelic.adventuraarcanacore.food;

import java.util.List;

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
 * The shoot of one bamboo species (metadata: BambooSpecies order): a cooking ingredient under HarvestCraft's
 * cropBambooshoot and listAllveggie, not eaten raw (fresh bamboo shoots are bitter and mildly toxic until cooked),
 * and planted on soil it grows into its shoot plant again.
 */
public final class ItemBambooShoot extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    ItemBambooShoot() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("adventuraarcanacore.bamboo_shoot");
        setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabFood);
    }

    static BambooSpecies species(int metadata) {
        BambooSpecies[] all = BambooSpecies.values();
        return all[Math.floorMod(metadata, all.length)];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.biomic.bamboo_shoot." + species(stack.getItemDamage()).id;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        BlockBambooShoot shoot = BambooShoots.shoot(species(stack.getItemDamage()));
        if (side != 1 || shoot == null
            || !player.canPlayerEdit(x, y, z, side, stack)
            || !player.canPlayerEdit(x, y + 1, z, side, stack)
            || !world.isAirBlock(x, y + 1, z)
            || !shoot.canBlockStay(world, x, y + 1, z)) return false;
        world.setBlock(x, y + 1, z, shoot, 0, 3);
        world.playSoundEffect(
            x + 0.5,
            y + 1.5,
            z + 0.5,
            shoot.stepSound.func_150496_b(),
            (shoot.stepSound.getVolume() + 1.0F) / 2.0F,
            shoot.stepSound.getPitch() * 0.8F);
        if (!player.capabilities.isCreativeMode) stack.stackSize--;
        return true;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (BambooSpecies species : BambooSpecies.values()) list.add(new ItemStack(item, 1, species.ordinal()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int metadata) {
        return icons[species(metadata).ordinal()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[BambooSpecies.values().length];
        for (BambooSpecies species : BambooSpecies.values()) icons[species.ordinal()] = PaintedSprite.register(
            register,
            "adventuraarcanacore:food/bamboo_shoot_item_" + species.id,
            source -> FoodPaint.shoot(source, "textures/items/bambooshootItem.png", species.culmTexture()));
    }
}
