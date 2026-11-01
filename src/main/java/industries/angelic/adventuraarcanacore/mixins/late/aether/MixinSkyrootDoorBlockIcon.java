package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * The Aether registers its Skyroot Door block with an item of its own, and WAILA shows that item before the door's
 * real one: its name had no translation (ours is in the lang file) and its icon was the door's lower half. Giving the
 * block an item icon name, as vanilla does for the cauldron, draws the door item's icon instead.
 */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.blocks.decorative.BlockAetherDoor")
public abstract class MixinSkyrootDoorBlockIcon extends BlockDoor {

    private MixinSkyrootDoorBlockIcon(Material material) {
        super(material);
    }

    @Override
    public String getItemIconName() {
        return "aetherii:Skyroot Door";
    }
}
