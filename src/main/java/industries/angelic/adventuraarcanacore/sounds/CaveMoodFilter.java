package industries.angelic.adventuraarcanacore.sounds;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import industries.angelic.adventuraarcanacore.Config;

/**
 * Mutes vanilla's random cave sounds unless {@link Config#caveMoods} is on. Et Futurum adds its modern ones to the same
 * event, so they go too. Runs last, so a mod that swaps the sound for its own (Netherlicious's and Biomic's Nether
 * moods) keeps it.
 */
public final class CaveMoodFilter {

    private static final String CAVE = "ambient.cave.cave";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlaySound(PlaySoundEvent17 event) {
        if (Config.caveMoods || event.result == null) return;
        ResourceLocation sound = event.result.getPositionedSoundLocation();
        if ("minecraft".equals(sound.getResourceDomain()) && CAVE.equals(sound.getResourcePath())) event.result = null;
    }
}
