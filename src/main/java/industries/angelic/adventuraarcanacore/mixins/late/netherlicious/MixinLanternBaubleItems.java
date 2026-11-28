package industries.angelic.adventuraarcanacore.mixins.late.netherlicious;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.compat.netherlicious.LanternBaubleItem;
import industries.angelic.adventuraarcanacore.compat.netherlicious.NetherliciousLanterns;

/**
 * Netherlicious registers most of its lanterns with a plain ItemBlock, which no bauble slot takes. Register those with
 * LanternBaubleItem, under the same name, so they can be worn on the belt.
 */
@Pseudo
@Mixin(targets = "DelirusCrux.Netherlicious.Common.BlockItemUtility.ModBlocks", remap = false)
public abstract class MixinLanternBaubleItems {

    @Redirect(
        method = "registerBlock(Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;",
        at = @At(
            value = "INVOKE",
            target = "Lcpw/mods/fml/common/registry/GameRegistry;registerBlock(Lnet/minecraft/block/Block;Ljava/lang/String;)Lnet/minecraft/block/Block;"),
        remap = false,
        require = 1)
    private static Block adventuraarcanacore$wearable(Block block, String name) {
        if (!NetherliciousLanterns.isWearable(block)) return GameRegistry.registerBlock(block, name);
        return GameRegistry.registerBlock(block, LanternBaubleItem.class, name);
    }
}
