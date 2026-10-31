package industries.angelic.adventuraarcanacore.compat.battletowers;

import net.minecraft.world.World;

/**
 * Battle Towers' fixed heights assume vanilla's ground at y=64: it looks for the surface from y=51 up, and builds
 * floors
 * up to y=120. Both follow the world type's ground level instead (192 in Biomic's tall worlds, 64 in vanilla).
 */
public final class BattleTowers {

    private BattleTowers() {}

    /** Where the surface search starts: 14 below the ground level, as y=50 is below vanilla's. */
    public static int searchStart(World world) {
        return world.provider.getAverageGroundLevel() - 14;
    }

    /** The tower's top: 56 above the ground level, as y=120 is above vanilla's, and at least three floors high. */
    public static int towerTop(World world, int surface) {
        return Math.max(world.provider.getAverageGroundLevel() + 56, surface - 6 + 3 * 7);
    }
}
