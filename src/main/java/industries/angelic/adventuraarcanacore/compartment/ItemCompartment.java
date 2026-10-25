package industries.angelic.adventuraarcanacore.compartment;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemCompartment extends ItemBlock {

    public ItemCompartment(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMetadata(int metadata) {
        return CompartmentTier.fromMetadata(metadata)
            .ordinal();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile.adventuraarcanacore.compartment." + CompartmentTier.fromMetadata(stack.getItemDamage()).key;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        CompartmentTier tier = CompartmentTier.fromMetadata(stack.getItemDamage());
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "adventuraarcanacore.compartment.capacity",
                tier.tabs,
                tier.pageSize,
                tier.capacity()));
    }
}
