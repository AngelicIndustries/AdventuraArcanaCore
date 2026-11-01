package industries.angelic.adventuraarcanacore.compat.aether;

import net.minecraft.item.Item;

/**
 * The Aether's Skyroot Door item asks for its icon as "skyroot Door", but the texture is "Skyroot Door.png", and paths
 * in a jar are case-sensitive, so it drew the missing texture. Its name key (item.skyroot_door_item.name) has no
 * translation either; ours is in the lang file.
 */
public final class AetherSkyrootDoor {

    private AetherSkyrootDoor() {}

    /** Before the item textures are stitched, which happens after mods load. */
    public static void fixIcon() {
        if (Item.itemRegistry.getObject("aether_legacy:skyroot_door_item") instanceof Item door)
            door.setTextureName("aetherii:Skyroot Door");
    }
}
