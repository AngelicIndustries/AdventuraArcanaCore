package industries.angelic.adventuraarcanacore.compat.reccomplex;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;

/**
 * Whether a structure would stand in a village. Villages are found where the world keeps them, its "Village"
 * structure data (vanilla's and Village Names' villages alike), by their ground plan: a start's saved height is the
 * one it had before its houses were set on the ground, so only a structure that reaches near the surface counts, and
 * dungeons deep under a village still generate.
 */
public final class VillageSites {

    /** A structure whose top is this far under the ground at its middle is clear of whatever stands on it. */
    private static final int DEPTH = 8;

    private VillageSites() {}

    /** coord and size are the structure's lower corner and extent, x, y, z. */
    public static boolean meets(World world, int[] coord, int[] size) {
        int minX = coord[0], minZ = coord[2], maxX = minX + size[0] - 1, maxZ = minZ + size[2] - 1;
        int top = coord[1] + size[1] - 1;
        if (top < world.getHeightValue((minX + maxX) >> 1, (minZ + maxZ) >> 1) - DEPTH) return false;
        if (!(world.perWorldStorage.loadData(MapGenStructureData.class, "Village") instanceof MapGenStructureData data))
            return false;
        NBTTagCompound villages = data.func_143041_a();
        for (Object key : villages.func_150296_c()) {
            int[] box = villages.getCompoundTag((String) key)
                .getIntArray("BB");
            if (box.length == 6 && box[0] <= maxX && box[3] >= minX && box[2] <= maxZ && box[5] >= minZ) return true;
        }
        return false;
    }
}
