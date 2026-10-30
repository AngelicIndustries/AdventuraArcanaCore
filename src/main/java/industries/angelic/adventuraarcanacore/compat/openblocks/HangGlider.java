package industries.angelic.adventuraarcanacore.compat.openblocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * The pack's hang glider (HANG_GLIDER_NERF). OpenBlocks pushes 0.03 forward and sinks 0.052 per tick while gliding, and
 * pushes 0.1 while diving; air drag makes the top speed about ten times the push.
 */
public final class HangGlider {

    public static final double GLIDE_PUSH = 0.02;
    public static final double GLIDE_SINK = -0.08;
    public static final double DIVE_PUSH = 0.04;
    /** Seconds in the air before the glider breaks. */
    public static final int DURABILITY = 240;

    private HangGlider() {}

    /** Wears the held glider by a point per second in the air; false once it is broken. */
    public static boolean wear(EntityPlayer player, Entity glider, ItemStack held, boolean deployed) {
        if (held.stackSize <= 0) return false;
        if (player.worldObj.isRemote || !deployed || glider.ticksExisted % 20 != 0) return true;
        held.damageItem(1, player);
        if (held.stackSize > 0) return true;
        ItemStack[] inventory = player.inventory.mainInventory;
        for (int i = 0; i < inventory.length; i++) if (inventory[i] == held) inventory[i] = null;
        return false;
    }
}
