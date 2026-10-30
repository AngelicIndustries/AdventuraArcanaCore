package industries.angelic.adventuraarcanacore.sounds;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.Config;

/**
 * Wild wolves howl at night in surface worlds, with Rhapsodia's howls; wild wolves nearby may join in. The pack's
 * MAtmos sounds leave out Rhapsodia's snow-biome howls, so howls come from real wolves.
 */
public final class WolfHowls {

    public static final String SOUND = AdventuraArcanaCore.MODID + ":mob.wolf.howl";
    /** One in this many ticks a wild wolf starts howling: about once in five minutes of night. */
    private static final int CHANCE = 6000;
    /** How far the others hear it and join in, horizontally. */
    private static final double CHORUS_RANGE = 24;
    /** 3 is heard to 48 blocks. */
    private static final float VOLUME = 3.0F;

    /** Wolves joining a howl, with the ticks until they do. Server thread only. */
    private final Map<EntityWolf, Integer> chorus = new WeakHashMap<>();

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityWolf)) return;
        EntityWolf wolf = (EntityWolf) event.entityLiving;
        if (wolf.worldObj.isRemote || !Config.wolfHowls) return;
        Integer delay = chorus.get(wolf);
        if (delay != null) {
            if (delay > 0) {
                chorus.put(wolf, delay - 1);
                return;
            }
            chorus.remove(wolf);
            if (canHowl(wolf)) howl(wolf);
            return;
        }
        Random random = wolf.getRNG();
        if (random.nextInt(CHANCE) != 0 || !canHowl(wolf)) return;
        howl(wolf);
        for (Object entity : wolf.worldObj
            .getEntitiesWithinAABB(EntityWolf.class, wolf.boundingBox.expand(CHORUS_RANGE, 8, CHORUS_RANGE))) {
            EntityWolf other = (EntityWolf) entity;
            if (other != wolf && random.nextBoolean() && !chorus.containsKey(other) && canHowl(other))
                chorus.put(other, 20 + random.nextInt(60));
        }
    }

    private static boolean canHowl(EntityWolf wolf) {
        World world = wolf.worldObj;
        long time = world.getWorldTime() % 24000L;
        return time >= 13000L && time < 23000L
            && world.provider.isSurfaceWorld()
            && !wolf.isDead
            && !wolf.isTamed()
            && !wolf.isAngry()
            && !wolf.isChild()
            && !wolf.isInWater()
            && wolf.ridingEntity == null;
    }

    private static void howl(EntityWolf wolf) {
        wolf.worldObj.playSoundAtEntity(
            wolf,
            SOUND,
            VOLUME,
            0.9F + wolf.getRNG()
                .nextFloat() * 0.2F);
        // No bark or pant over the howl.
        wolf.livingSoundTime = -wolf.getTalkInterval();
    }
}
