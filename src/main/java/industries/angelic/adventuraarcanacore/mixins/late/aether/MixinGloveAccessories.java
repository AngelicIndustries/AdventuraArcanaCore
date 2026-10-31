package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.item.Item;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * The Aether registers its netherite gloves as an accessory under the copper recipe option and its copper gloves under
 * the netherite one, so with only one option on its gloves can't be worn. Register each under its own option.
 */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.registry.AetherRegistries", remap = false)
public abstract class MixinGloveAccessories {

    @Redirect(
        method = "initializeAccessories",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETSTATIC,
            target = "Lcom/gildedgames/the_aether/items/ItemsAether;netherite_gloves:Lnet/minecraft/item/Item;",
            ordinal = 0),
        remap = false,
        require = 1)
    private static Item adventuraarcanacore$copperGloves() {
        return GameRegistry.findItem("aether_legacy", "copper_gloves");
    }

    @Redirect(
        method = "initializeAccessories",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETSTATIC,
            target = "Lcom/gildedgames/the_aether/items/ItemsAether;copper_gloves:Lnet/minecraft/item/Item;",
            ordinal = 0),
        remap = false,
        require = 1)
    private static Item adventuraarcanacore$netheriteGloves() {
        return GameRegistry.findItem("aether_legacy", "netherite_gloves");
    }
}
