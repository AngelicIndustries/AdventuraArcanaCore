package industries.angelic.adventuraarcanacore.mixins.late.natura;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.Config;

/**
 * Natura's wild berry bushes are not generated in Biomic's overworld while HarvestCraft's berries grow wild there
 * (wildFoodInBiomic): one provider per plant. Harvesting, spreading and growing are untouched.
 */
@Pseudo
@Mixin(targets = "mods.natura.worldgen.BerryBushGen", remap = false)
public abstract class MixinNaturaBerryOwnership {

    @Inject(method = { "generate", "func_76484_a" }, at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$noWildBerries(World world, Random random, int x, int y, int z,
        CallbackInfoReturnable<Boolean> result) {
        if (Config.wildFoodInBiomic && world != null
            && !world.isRemote
            && world.provider.dimensionId == 0
            && "biomic".equals(
                world.getWorldInfo()
                    .getTerrainType()
                    .getWorldTypeName()))
            result.setReturnValue(false);
    }
}
