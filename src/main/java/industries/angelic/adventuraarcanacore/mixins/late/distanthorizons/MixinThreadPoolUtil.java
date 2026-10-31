package industries.angelic.adventuraarcanacore.mixins.late.distanthorizons;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.seibel.distanthorizons.common.util.threading.ServerThreadTaskHandler;
import com.seibel.distanthorizons.core.util.threading.ThreadPoolUtil;

/**
 * Distant Horizons' world generation threads wait, uninterruptibly, on tasks it queued for the server thread, and its
 * shutdown gives each thread pool 5 seconds to finish. When its world closes the server thread runs none of them: a
 * paused single-player game does not tick, and a stopping server cancels them only once it has stopped, after this
 * wait. Quitting a single-player world held the game for 10 seconds (long enough for the desktop to report it not
 * responding), a server's stop for 5. The world is closed when the pools shut down, so nothing waits on those tasks:
 * they are cancelled first, as the server would cancel them seconds later.
 */
@Mixin(value = ThreadPoolUtil.class, remap = false)
public abstract class MixinThreadPoolUtil {

    @Inject(method = "shutdownThreadPools", at = @At("HEAD"), require = 1)
    private static void adventuraarcanacore$cancelServerTasks(CallbackInfo callback) {
        ServerThreadTaskHandler.INSTANCE.cancelPendingTasks();
    }
}
