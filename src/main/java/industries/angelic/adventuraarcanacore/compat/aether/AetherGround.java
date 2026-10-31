package industries.angelic.adventuraarcanacore.compat.aether;

import net.minecraft.entity.Entity;

/** Whether a player stands, for the Aether's wings. */
public final class AetherGround {

    /** How far under the feet ground still counts, in blocks; a client knows other players' heights to 1/32. */
    private static final double REACH = 0.05;

    private AetherGround() {}

    /**
     * {@code entity.onGround}, except on a client, where the server never says whether other players stand and it
     * stays false for them: there, whether anything is just under the feet.
     */
    public static boolean standing(Entity entity) {
        if (entity.onGround || entity.worldObj == null || !entity.worldObj.isRemote) return entity.onGround;
        return !entity.worldObj
            .getCollidingBoundingBoxes(entity, entity.boundingBox.getOffsetBoundingBox(0.0, -REACH, 0.0))
            .isEmpty();
    }
}
