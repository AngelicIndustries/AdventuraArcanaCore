package industries.angelic.adventuraarcanacore.food;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;

/** Item form of the wild bushes and pods, one per metadata. */
public final class ItemBlockFood extends ItemBlockWithMetadata {

    public ItemBlockFood(Block block) {
        super(block, block);
    }
}
