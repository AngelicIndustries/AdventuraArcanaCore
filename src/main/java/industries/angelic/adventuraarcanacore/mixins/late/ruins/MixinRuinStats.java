package industries.angelic.adventuraarcanacore.mixins.late.ruins;

import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * EndlessIDs' Ruins compat moves the "any biome" slot (RuinsMod.BIOME_NONE, 500) to the end of its extended biome
 * list, but RuinStats still allocates BIOME_NONE + 1 counters. Size them for the whole biome list instead.
 */
@Pseudo
@Mixin(targets = "atomicstryker.ruins.common.RuinStats", remap = false)
public abstract class MixinRuinStats {

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 501))
    private int adventuraarcanacore$fitExtendedBiomes(int size) {
        return Math.max(size, BiomeGenBase.getBiomeGenArray().length + 1);
    }
}
