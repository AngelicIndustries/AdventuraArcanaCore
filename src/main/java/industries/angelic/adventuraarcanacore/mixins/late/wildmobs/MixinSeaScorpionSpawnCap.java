package industries.angelic.adventuraarcanacore.mixins.late.wildmobs;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * Wild Mobs spawns sea scorpions as water creatures, but they are monsters (IMob) rather than EntityWaterMobs, so the
 * water creature cap never counted them and spawning added more without end (tens of thousands in Biomic's deep
 * oceans), while they filled the monster cap instead. They now count as water creatures only.
 */
@Pseudo
@Mixin(targets = "com.wildmobsmod.entity.monster.seascorpion.EntitySeaScorpion", remap = false)
public abstract class MixinSeaScorpionSpawnCap {

    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        // As Forge's Entity.isCreatureType, which leaves mobs that never despawn out of the spawn caps
        return type == EnumCreatureType.waterCreature
            && !(forSpawnCount && ((EntityLiving) (Object) this).isNoDespawnRequired());
    }
}
