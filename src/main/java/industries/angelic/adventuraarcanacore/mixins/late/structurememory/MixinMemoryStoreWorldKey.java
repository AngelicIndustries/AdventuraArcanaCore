package industries.angelic.adventuraarcanacore.mixins.late.structurememory;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.integrated.IntegratedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

/**
 * Structure Memory keys a singleplayer world's memories by its seed alone, and a server's by its spawn point, so saves
 * sharing a seed, or servers sharing a spawn, share one memory. Add the save's folder, which renaming a world keeps,
 * or the server's address. Client only.
 */
@Pseudo
@Mixin(targets = "xsoras.structurememory.memory.MemoryStore", remap = false)
public abstract class MixinMemoryStoreWorldKey {

    @ModifyReturnValue(method = "currentWorldKey", at = @At("RETURN"), remap = false, require = 1)
    private static String adventuraarcanacore$perSave(String key) {
        if (key == null) return null;
        Minecraft mc = Minecraft.getMinecraft();
        IntegratedServer server = mc.getIntegratedServer();
        if (server != null) return key + "-" + adventuraarcanacore$sanitise(server.getFolderName());
        ServerData data = mc.func_147104_D();
        return data != null && data.serverIP != null ? key + "-" + adventuraarcanacore$sanitise(data.serverIP) : key;
    }

    private static String adventuraarcanacore$sanitise(String name) {
        return name.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9._-]", "_");
    }
}
