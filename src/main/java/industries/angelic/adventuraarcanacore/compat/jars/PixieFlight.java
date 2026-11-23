package industries.angelic.adventuraarcanacore.compat.jars;

import net.minecraft.util.MathHelper;

/**
 * How a pixie flies about in its jar: slow loops that never touch the glass, facing where it goes as a free pixie does.
 * The same for a jar set down and one in a slot, so both look alike.
 */
final class PixieFlight {

    /** The pixie's size in a jar; its wings, spread, reach nine pixels from its middle. */
    static final float SCALE = 0.45F;

    private PixieFlight() {}

    /**
     * Where the pixie is at this time, in ticks: x, y and z of its feet within the jar's block, and its yaw. A seed
     * puts jars out of step.
     */
    static float[] at(float time, long seed) {
        float t = time + (seed & 1023);
        float x = 0.5F + 0.05F * MathHelper.sin(t * 0.071F);
        float z = 0.5F + 0.05F * MathHelper.cos(t * 0.053F);
        float y = 0.16F + 0.09F * MathHelper.sin(t * 0.093F) + 0.02F * MathHelper.sin(t * 0.31F);
        // Facing its way, from the loops' slopes
        float dx = 0.071F * MathHelper.cos(t * 0.071F);
        float dz = -0.053F * MathHelper.sin(t * 0.053F);
        float yaw = -(float) Math.toDegrees(Math.atan2(dx, dz));
        return new float[] { x, y, z, yaw };
    }

    /** The middle of the pixie's body above its feet. */
    static float bodyHeight() {
        return 0.375F * SCALE;
    }
}
