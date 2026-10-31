package industries.angelic.adventuraarcanacore.mixins.late.battletowers;

import java.io.PrintStream;
import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.Config;
import industries.angelic.adventuraarcanacore.compat.battletowers.BattleTowers;

/**
 * Battle Towers' site picker: its surface search follows the world's ground level, a chunk becomes a site only by
 * Config.battleTowerChance, and the two lines it printed for every new chunk go to the debug log.
 */
@Pseudo
@Mixin(targets = "atomicstryker.battletowers.common.WorldGenHandler", remap = false)
public abstract class MixinBattleTowersHandler {

    @ModifyConstant(method = "getSurfaceBlockHeight", constant = @Constant(intValue = 50), remap = false, require = 1)
    private int adventuraarcanacore$searchStart(int original, @Local(argsOnly = true) World world) {
        return BattleTowers.searchStart(world);
    }

    @Inject(method = "generateSurface", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void adventuraarcanacore$chance(World world, Random random, int x, int z, CallbackInfo ci) {
        if (Config.battleTowerChance < 1 && random.nextDouble() >= Config.battleTowerChance) ci.cancel();
    }

    @Redirect(
        method = { "generateSurface", "canTowerSpawnAt" },
        at = @At(
            value = "INVOKE",
            target = "Ljava/io/PrintStream;printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;",
            remap = false),
        remap = false,
        require = 2)
    private PrintStream adventuraarcanacore$quiet(PrintStream out, String format, Object[] args) {
        AdventuraArcanaCore.debug(
            "Battle Towers: " + String.format(format, args)
                .trim());
        return out;
    }
}
