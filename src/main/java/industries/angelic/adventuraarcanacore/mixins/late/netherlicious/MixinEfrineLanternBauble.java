package industries.angelic.adventuraarcanacore.mixins.late.netherlicious;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import industries.angelic.adventuraarcanacore.compat.netherlicious.NetherliciousLanterns;

/**
 * Netherlicious' Efrine blocks share one fireproof item class, whose lanterns become belt baubles here. The other
 * Efrine blocks fit no slot.
 */
@Pseudo
@Mixin(targets = "DelirusCrux.Netherlicious.Common.ItemBlocks.ItemBlockEfrine", remap = false)
public abstract class MixinEfrineLanternBauble extends ItemBlock implements IBaubleExpanded {

    @Unique
    private static final String[] adventuraarcanacore$NONE = {};

    private MixinEfrineLanternBauble(Block block) {
        super(block);
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return NetherliciousLanterns.isWearable(field_150939_a) ? NetherliciousLanterns.BAUBLE_TYPES
            : adventuraarcanacore$NONE;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.BELT;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase wearer) {}

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase wearer) {
        return NetherliciousLanterns.isWearable(field_150939_a);
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase wearer) {
        return true;
    }

    @Inject(
        method = {
            "addInformation(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;Z)V",
            "func_77624_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;Z)V" },
        at = @At("TAIL"),
        remap = false,
        require = 1)
    private void adventuraarcanacore$slotInformation(ItemStack stack, EntityPlayer player, List<String> tooltip,
        boolean advanced, CallbackInfo ci) {
        if (NetherliciousLanterns.isWearable(field_150939_a))
            BaubleItemHelper.addSlotInformation(tooltip, NetherliciousLanterns.BAUBLE_TYPES);
    }
}
