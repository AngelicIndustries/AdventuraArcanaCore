package industries.angelic.adventuraarcanacore.mixins.late.reccomplex;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.adventuraarcanacore.compat.reccomplex.VillageSites;

/**
 * Recurrent Complex places its natural structures without knowing about villages, so a graveyard could stand inside a
 * village's house. Before it builds a suggested structure it asks its own event bus whether to cancel; the answer is
 * also yes where the structure would stand in a village (VillageSites).
 */
@Pseudo
@Mixin(targets = "ivorius.reccomplex.worldgen.StructureGenerator", remap = false)
public abstract class MixinStructureGeneratorVillages {

    @ModifyExpressionValue(
        method = "instantly",
        at = @At(
            value = "INVOKE",
            target = "Lcpw/mods/fml/common/eventhandler/EventBus;post(Lcpw/mods/fml/common/eventhandler/Event;)Z",
            ordinal = 0,
            remap = false),
        remap = false,
        require = 1)
    private static boolean adventuraarcanacore$village(boolean cancelled, @Local(argsOnly = true) World world,
        @Local(ordinal = 0) int[] size, @Local(ordinal = 1) int[] coord) {
        return cancelled || VillageSites.meets(world, coord, size);
    }
}
