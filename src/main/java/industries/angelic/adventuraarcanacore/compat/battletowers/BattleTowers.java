package industries.angelic.adventuraarcanacore.compat.battletowers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * Battle Towers' fixed heights assume vanilla's ground at y=64: it looked for the surface from y=51 up, taking the
 * first air it met, and builds floors up to y=120. The surface is now found from the top down, and the floors follow
 * the world type's ground level (192 in Biomic's tall worlds, 64 in vanilla).
 */
public final class BattleTowers {

    private BattleTowers() {}

    /**
     * The ground block of a column: the highest one that isn't air or growth on the ground (logs, leaves, plants, snow
     * cover). Searching up from a fixed height stopped at the first cave above it, so in tall terrain towers stood on
     * cave floors and rose half buried through the ground.
     */
    public static int surface(World world, int x, int z) {
        for (int y = world.getHeightValue(x, z); y > 0; y--) {
            Block block = world.getBlock(x, y, z);
            if (!growth(world, block, x, y, z)) return y;
        }
        return 0;
    }

    private static boolean growth(World world, Block block, int x, int y, int z) {
        Material material = block.getMaterial();
        return block.isAir(world, x, y, z) || material == Material.leaves
            || material == Material.plants
            || material == Material.vine
            || material == Material.snow
            || material == Material.web
            || block.isWood(world, x, y, z)
            || block.isLeaves(world, x, y, z);
    }

    /** The tower's top: 56 above the ground level, as y=120 is above vanilla's, and at least three floors high. */
    public static int towerTop(World world, int surface) {
        return Math.max(world.provider.getAverageGroundLevel() + 56, surface - 6 + 3 * 7);
    }
}
