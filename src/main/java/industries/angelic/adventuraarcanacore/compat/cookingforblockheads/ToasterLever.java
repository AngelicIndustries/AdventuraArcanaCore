package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

/**
 * The toaster's lever, which Cooking for Blockheads only snapped between up and down. It now goes down over a few
 * ticks when the toaster starts and pops back up quickly when it stops.
 */
public final class ToasterLever {

    /** Ticks for the lever to go all the way down, and to pop back up. */
    public static final int DOWN_TICKS = 4, UP_TICKS = 2;
    /** Cooking for Blockheads' own travel of the lever and the bread, in its renderer's units. */
    public static final float BUTTON_TRAVEL = 0.17F, BREAD_TRAVEL = 0.08F;
    public static final String LOWER = "adventuraarcanacore:block.toaster.lower",
        POP = "adventuraarcanacore:block.toaster.pop";
    /** Both sounds play a little higher than recorded, with some variation. */
    public static final float PITCH = 1.08F, PITCH_SPREAD = 0.06F;
    /** Whether the toaster runs and its remaining ticks, saved beside Cooking for Blockheads' own tags. */
    public static final String ACTIVE_TAG = "AACActive", TICKS_TAG = "AACToastTicks";

    private ToasterLever() {}

    /** A toaster whose lever moves. 0 is up, 1 is down. */
    public interface Animated {

        float adventuraarcanacore$lever(float partialTicks);
    }

    /** The lever's position one tick later. */
    public static float step(float lever, boolean active) {
        return active ? Math.min(1, lever + 1F / DOWN_TICKS) : Math.max(0, lever - 1F / UP_TICKS);
    }

    /** The bread's height in the toaster's renderer, following the lever. */
    public static float breadY(float lever) {
        return 0.025F - BREAD_TRAVEL * lever;
    }
}
