package industries.angelic.adventuraarcanacore.mixins.late.bibliocraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * An easter egg: anything named Nuchaz, ShabbyQ, Ekacfeeb12 or murphyobrian drops a Screw Gun, Tape Measure or Reading
 * Glasses when it dies. With those items switched off in BiblioCraft's config, the drops are stacks without an item,
 * which crash
 * whoever picks them up or draws them. Drop nothing instead.
 */
@Pseudo
@Mixin(targets = "jds.bibliocraft.EventDeathDrop", remap = false)
public abstract class MixinEasterEggDrops {

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true, require = 1)
    private void adventuraarcanacore$skipMissingItems(LivingDeathEvent event, ItemStack stack, CallbackInfo ci) {
        if (stack == null || stack.getItem() == null) ci.cancel();
    }
}
