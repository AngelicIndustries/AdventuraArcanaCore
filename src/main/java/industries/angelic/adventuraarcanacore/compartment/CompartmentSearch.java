package industries.angelic.adventuraarcanacore.compartment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class CompartmentSearch {

    private CompartmentSearch() {}

    public static List<Integer> findSlots(IInventory inventory, String query, boolean includeItems,
        boolean includeBlocks, boolean sortByName) {
        List<Integer> matches = new ArrayList<>();
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) continue;
            boolean block = Block.getBlockFromItem(stack.getItem()) != Blocks.air;
            if ((block ? includeBlocks : includeItems) && stack.getDisplayName()
                .toLowerCase(Locale.ROOT)
                .contains(lowerQuery)) matches.add(i);
        }
        if (sortByName) matches.sort(
            Comparator.comparing(
                i -> inventory.getStackInSlot(i)
                    .getDisplayName()
                    .toLowerCase(Locale.ROOT)));
        return matches;
    }
}
