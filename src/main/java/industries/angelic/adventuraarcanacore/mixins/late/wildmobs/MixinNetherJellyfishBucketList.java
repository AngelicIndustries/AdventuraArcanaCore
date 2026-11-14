package industries.angelic.adventuraarcanacore.mixins.late.wildmobs;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Wild Mobs' water and lava jellyfish buckets are one class, and both listed the six water jellyfish colors in the
 * creative tabs and NEI. The lava bucket's six looked and were named like the water buckets but released only into
 * lava, while the Nether Medusa Bucket a lava bucket really holds (variant 6) was not listed at all. The lava bucket
 * now lists just that. Client only.
 */
@Pseudo
@Mixin(targets = "com.wildmobsmod.items.ItemJellyfishBucket", remap = false)
public abstract class MixinNetherJellyfishBucketList {

    /** The Nether Medusa, the only jellyfish a lava bucket catches. */
    private static final int NETHER_VARIANT = 6;

    @Shadow
    private boolean nether;

    // The target is not on the compile classpath: name its MCP and shipped SRG methods.
    @Inject(
        method = { "getSubItems", "func_150895_a" },
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        remap = false)
    private void adventuraarcanacore$netherOnly(Item item, CreativeTabs tab, List<ItemStack> list,
        CallbackInfo callback) {
        if (!nether) return;
        list.add(new ItemStack(item, 1, NETHER_VARIANT));
        callback.cancel();
    }
}
