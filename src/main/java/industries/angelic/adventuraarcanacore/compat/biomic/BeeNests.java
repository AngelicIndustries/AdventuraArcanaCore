package industries.angelic.adventuraarcanacore.compat.biomic;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.entities.EntityBee;

/**
 * EFR's bee nest as Biomic's plant layer hangs it on a trunk (proposal v3: the only wild hive), with two or three bees
 * inside. The
 * bees are written as data, not spawned, so a nest is the same on every generation.
 */
final class BeeNests {

    private BeeNests() {}

    /** Whether EFR's bees are enabled (its nest block is registered). */
    public static boolean available() {
        return ModBlocks.BEE_NEST.isEnabled() && EntityList.classToStringMapping.containsKey(EntityBee.class);
    }

    /** A nest whose front faces {@code facing} (2 north, 3 south, 4 west, 5 east). */
    public static String state(int facing) {
        return "etfuturum:bee_nest[legacy_meta=" + facing + "]";
    }

    /** The nest's tile data with its bees; {@code hash} varies their count and time inside. */
    public static NBTTagCompound payload(int x, int y, int z, int facing, long hash) {
        Block nest = ModBlocks.BEE_NEST.get();
        TileEntity tile = nest.createTileEntity(null, facing);
        tile.xCoord = x;
        tile.yCoord = y;
        tile.zCoord = z;
        NBTTagCompound nbt = new NBTTagCompound();
        tile.writeToNBT(nbt);
        NBTTagList bees = new NBTTagList();
        String id = (String) EntityList.classToStringMapping.get(EntityBee.class);
        int count = 2 + (int) ((hash >>> 8) & 1);
        for (int i = 0; i < count; i++) {
            NBTTagCompound data = new NBTTagCompound();
            data.setString("id", id);
            NBTTagCompound bee = new NBTTagCompound();
            bee.setTag("EntityData", data);
            bee.setInteger("TicksInHive", (int) ((hash >>> (16 + 9 * i)) % 600));
            bee.setInteger("MinOccupationTicks", 600);
            bees.appendTag(bee);
        }
        nbt.setTag("Bees", bees);
        return nbt;
    }
}
