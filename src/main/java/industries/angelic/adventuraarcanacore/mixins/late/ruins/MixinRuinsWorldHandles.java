package industries.angelic.adventuraarcanacore.mixins.late.ruins;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ruins keeps one handle per dimension (its world's folder, chunk log and placed ruins) from mod init on, never
 * per world. In single player a world opened after another in the same game ran on the first world's handle: it
 * skipped the chunks the first world's log held, refused every spot near the first world's ruins, and wrote its ruins
 * into the first world's file, so a seed did not give the same ruins. The handles last one server here.
 */
@Pseudo
@Mixin(targets = "atomicstryker.ruins.common.RuinsMod", remap = false)
public abstract class MixinRuinsWorldHandles {

    @Shadow
    private ConcurrentHashMap<Integer, Object> generatorMap;

    @Unique
    private MinecraftServer adventuraarcanacore$server;

    @Inject(method = "getWorldHandle", at = @At("HEAD"), require = 1)
    private void adventuraarcanacore$handlesOfThisServer(World world, CallbackInfoReturnable<Object> callback) {
        if (world.isRemote) return;
        MinecraftServer server = MinecraftServer.getServer();
        if (server == adventuraarcanacore$server) return;
        generatorMap.clear();
        adventuraarcanacore$server = server;
    }
}
