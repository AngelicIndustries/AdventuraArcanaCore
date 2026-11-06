package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

/**
 * The toaster took the bread but reported the click as unused, so the game went on to use the bread: a hungry player
 * started eating it. Bread the toaster can toast now ends the click there, whether it went in or the toaster was full.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.block.BlockToaster", remap = false)
public abstract class MixinToasterNoEating {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" }, remap = false, require = 1)
    private boolean adventuraarcanacore$keepBread(World world, int x, int y, int z, EntityPlayer player, int side,
        float hitX, float hitY, float hitZ, Operation<Boolean> original) {
        ItemStack held = player.getHeldItem();
        // Asked first: loading the last bread empties the hand.
        boolean toastable = held != null && AccessorCookingRegistry.adventuraarcanacore$toastOutput(held) != null;
        return original.call(world, x, y, z, player, side, hitX, hitY, hitZ) || toastable;
    }
}
