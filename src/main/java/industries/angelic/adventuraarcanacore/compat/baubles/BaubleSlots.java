package industries.angelic.adventuraarcanacore.compat.baubles;

import baubles.api.expanded.BaubleExpandedSlots;

/** Direct calls into Baubles Expanded, isolated so nothing links them without it. */
public final class BaubleSlots {

    private BaubleSlots() {}

    /**
     * The Aether (in Baubles Expanded's menu) and Uralte Zauber both use rings, amulets and charms. Asking for minimums
     * in pre-initialization adds slots the automatic way, so every other mod's requests still count.
     */
    public static void requestShared() {
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.ringType, 4);
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.amuletType, 2);
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.charmType, 3);
    }
}
