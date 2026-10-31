package industries.angelic.adventuraarcanacore.mixins.late.etfuturum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ganymedes01.etfuturum.core.handlers.ServerEventHandler;

/**
 * Et Futurum turns falling concrete powder into concrete while it iterates the set of falling powder, and placing the
 * concrete can add more falling powder to that set (block updates, a chunk loading its entities): the server crashed
 * with a ConcurrentModificationException. Iterate a copy; removing still empties the real set, and may repeat, as Et
 * Futurum removes twice when the block below is water too. Powder added meanwhile is checked next tick.
 */
@Mixin(value = ServerEventHandler.class, remap = false)
public abstract class MixinConcreteTracking {

    @Redirect(
        method = "doConcreteTracking",
        at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"),
        require = 1)
    private Iterator<Object> adventuraarcanacore$snapshot(Set<Object> tracked) {
        Iterator<Object> copy = new ArrayList<>(tracked).iterator();
        return new Iterator<Object>() {

            private Object current;

            @Override
            public boolean hasNext() {
                return copy.hasNext();
            }

            @Override
            public Object next() {
                return current = copy.next();
            }

            @Override
            public void remove() {
                tracked.remove(current);
            }
        };
    }
}
