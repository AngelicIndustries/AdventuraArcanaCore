package industries.angelic.adventuraarcanacore.food;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * A Madake grown from its shoot: Regions Unexplored's bamboo tree (Biomic's blocks) as the Bamboo Forest draws it
 * (ModernRuTreeFeatures.bamboo, regions_unexplored:tree/bamboo): a culm of 12 to 19 bamboo logs, two in three leafy,
 * under a crown of crosses, squares and one wide layer.
 */
final class MadakeTree {

    private static final int[][] CROSS = { { 0, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 }, { -1, 0 } };
    private static final int[][] CORNERS = { { 1, -1 }, { -1, -1 }, { 1, 1 }, { -1, 1 } };
    private static final int[][] RIM = { { 0, -2 }, { 1, -2 }, { -1, -2 }, { 0, 2 }, { 1, 2 }, { -1, 2 }, { 2, 0 },
        { 2, -1 }, { 2, 1 }, { -2, 0 }, { -2, -1 }, { -2, 1 } };
    /** The crown's layers from three below the top: 0 cross, 1 cross with some corners, 2 square, 3 wide. */
    private static final int[] CROWN = { 0, 1, 2, 3, 2, 1, 0, 0 };

    private MadakeTree() {}

    /** Grows the tree with its base at (x, y, z), the shoot's cell; false (nothing written) without room. */
    static boolean grow(World world, Random random, int x, int y, int z) {
        Block log = biomic("ru_imported_bamboo_log");
        Block leaves = biomic("ru_imported_bamboo_leaves");
        if (log == null || leaves == null) return false;
        int height = 12 + random.nextInt(8);
        int top = y + height;
        if (top + 7 >= world.getHeight()) return false;
        for (int at = y + 1; at <= top; at++) if (!world.isAirBlock(x, at, z)) return false;
        for (int at = y; at <= top; at++) world.setBlock(x, at, z, log, random.nextInt(3) == 0 ? 0 : 1, 2);
        int layer = top - 3;
        for (int pattern : CROWN) {
            for (int[] offset : CROSS) leaf(world, leaves, x + offset[0], layer, z + offset[1]);
            if (pattern >= 1) for (int[] offset : CORNERS)
                if (pattern != 1 || random.nextInt(3) == 0) leaf(world, leaves, x + offset[0], layer, z + offset[1]);
            if (pattern == 3) for (int[] offset : RIM) leaf(world, leaves, x + offset[0], layer, z + offset[1]);
            layer++;
        }
        leaf(world, leaves, x, layer, z);
        leaf(world, leaves, x, layer + 1, z);
        return true;
    }

    /** Biomic's block, or null without Biomic: then a Madake shoot waits, fully grown. */
    private static Block biomic(String name) {
        Block block = Block.getBlockFromName("biomic:" + name);
        return block == null || block == net.minecraft.init.Blocks.air ? null : block;
    }

    private static void leaf(World world, Block leaves, int x, int y, int z) {
        if (world.isAirBlock(x, y, z)) world.setBlock(x, y, z, leaves, 0, 2);
    }
}
