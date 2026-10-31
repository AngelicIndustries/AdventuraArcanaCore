package industries.angelic.adventuraarcanacore.mixins.late.battletowers;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.adventuraarcanacore.compat.battletowers.TowerSites;

/**
 * Battle Towers keeps one list of towers for whichever world it last generated in. Loading any other world replaced
 * that list with the other world's file, unsaved, and every world's save wrote the list into its own file, so towers
 * were forgotten and new ones went up next to them. Its generator already swaps the list properly when it moves to
 * another world, saving the old one first; a world's save now only writes the list that belongs to it. A tower also
 * skips a site where Recurrent Complex or Ruins already built.
 */
@Pseudo
@Mixin(targets = "atomicstryker.battletowers.common.WorldGenHandler", remap = false)
public abstract class MixinBattleTowersSites {

    @Shadow
    private static World lastWorld;

    @Inject(method = "eventWorldLoad", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$keepList(WorldEvent.Load event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "eventWorldSave", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$saveOwnList(WorldEvent.Save event, CallbackInfo ci) {
        if (event.world != lastWorld) ci.cancel();
    }

    @Inject(method = "attemptToSpawnTower", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$clearSite(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) World world,
        @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y,
        @Local(argsOnly = true, ordinal = 2) int z) {
        if (TowerSites.occupied(world, x, y, z)) cir.setReturnValue(false);
    }
}
