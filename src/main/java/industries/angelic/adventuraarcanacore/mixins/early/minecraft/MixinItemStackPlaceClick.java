package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.chests.ChestPairs;

/** Notes the block face a player uses an item on, which a chest placed by it goes against. */
@Mixin(ItemStack.class)
public abstract class MixinItemStackPlaceClick {

    @Inject(method = "tryPlaceItemIntoWorld", at = @At("HEAD"))
    private void adventuraarcanacore$click(EntityPlayer player, World world, int x, int y, int z, int side, float hitX,
        float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        ChestPairs.clicking(player, x, y, z, side);
    }

    @Inject(method = "tryPlaceItemIntoWorld", at = @At("RETURN"))
    private void adventuraarcanacore$clicked(EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        ChestPairs.clicked();
    }
}
