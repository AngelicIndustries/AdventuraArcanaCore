package industries.angelic.adventuraarcanacore.compat.jars;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import twilightforest.block.TFBlocks;
import twilightforest.entity.passive.EntityTFMobileFirefly;

/**
 * What using a critter jar does, decided on the server; the server's answer corrects the client's guess as for any
 * refused use. Biomes O' Plenty's pixie jar is set down when used, and its own sneak-use release gets the uncorking.
 * Twilight Forest's firefly jar, sneak-used, lets the firefly out and gives back the glass bottle it was made with.
 */
public class JarReleases {

    private final boolean pixies;
    private final boolean fireflies;

    JarReleases(boolean pixies, boolean fireflies) {
        this.pixies = pixies;
        this.fireflies = fireflies;
    }

    @SubscribeEvent
    public void interact(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        World world = player.worldObj;
        // Cancelling on the client would keep the use from reaching the server at all
        if (world.isRemote) return;
        boolean onBlock = event.action == Action.RIGHT_CLICK_BLOCK;
        if (!onBlock && event.action != Action.RIGHT_CLICK_AIR) return;
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null) return;

        if (pixies && held.getItem() == PixieJars.bopJarFilled() && held.getItemDamage() == PixieJars.PIXIE_META) {
            if (!onBlock) return;
            int[] at = beside(world, event.x, event.y, event.z, event.face);
            if (!player.isSneaking()) {
                if (setDown(world, player, held, at, event.face)) event.setCanceled(true);
            } else if (player.dimension == 0) {
                // BOP lets the pixie out (only in the Overworld): it's uncorked
                world.playSoundEffect(at[0] + 0.5, at[1] + 0.5, at[2] + 0.5, PixieJars.UNCORK, 1.0F, 1.0F);
            }
            return;
        }

        if (fireflies && player.isSneaking() && held.getItem() == Item.getItemFromBlock(TFBlocks.fireflyJar)) {
            double x, y, z;
            if (onBlock) {
                int[] at = beside(world, event.x, event.y, event.z, event.face);
                x = at[0] + 0.5;
                y = at[1] + 0.5;
                z = at[2] + 0.5;
            } else {
                Vec3 look = player.getLookVec();
                x = player.posX + look.xCoord * 1.5;
                y = player.posY + player.getEyeHeight() + look.yCoord * 1.5;
                z = player.posZ + look.zCoord * 1.5;
            }
            EntityTFMobileFirefly firefly = new EntityTFMobileFirefly(world);
            firefly.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
            world.spawnEntityInWorld(firefly);
            world.playSoundEffect(x, y, z, PixieJars.UNCORK, 1.0F, 1.0F);
            swap(player, held, new ItemStack(Items.glass_bottle));
            event.setCanceled(true);
        }
    }

    /** Where a use on a block's face puts something: in place of a replaceable block, else beside the face. */
    private static int[] beside(World world, int x, int y, int z, int face) {
        if (world.getBlock(x, y, z)
            .isReplaceable(world, x, y, z)) return new int[] { x, y, z };
        ForgeDirection side = ForgeDirection.getOrientation(face);
        return new int[] { x + side.offsetX, y + side.offsetY, z + side.offsetZ };
    }

    /** Sets the pixie jar down where the jar was used, as an item block would be placed. */
    private static boolean setDown(World world, EntityPlayer player, ItemStack held, int[] at, int face) {
        Block jar = PixieJars.pixieJar;
        if (!world.getBlock(at[0], at[1], at[2])
            .isReplaceable(world, at[0], at[1], at[2]) || !player.canPlayerEdit(at[0], at[1], at[2], face, held)
            || !jar.canPlaceBlockAt(world, at[0], at[1], at[2])) return false;
        world.setBlock(at[0], at[1], at[2], jar);
        world.playSoundEffect(
            at[0] + 0.5,
            at[1] + 0.5,
            at[2] + 0.5,
            jar.stepSound.func_150496_b(),
            (jar.stepSound.getVolume() + 1.0F) / 2.0F,
            jar.stepSound.getPitch() * 0.8F);
        if (!player.capabilities.isCreativeMode && --held.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
        return true;
    }

    /** Turns one item of the held stack into another, as a bucket does when it's emptied. */
    private static void swap(EntityPlayer player, ItemStack held, ItemStack result) {
        if (player.capabilities.isCreativeMode) {
            if (!player.inventory.addItemStackToInventory(result)) player.dropPlayerItemWithRandomChoice(result, false);
            return;
        }
        if (--held.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, result);
        } else if (!player.inventory.addItemStackToInventory(result)) {
            player.dropPlayerItemWithRandomChoice(result, false);
        }
    }
}
