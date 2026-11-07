package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.compat.realistictorches.TorchPaint;
import industries.angelic.adventuraarcanacore.compat.realistictorches.Torches;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/**
 * The Aether's torches, on Skyroot sticks, look like vanilla's torch with their flame's colours, as the unlit wooden
 * torch they go out into and the other flames' torches do (TorchPaint).
 */
@Pseudo
@Mixin(
    targets = { "com.gildedgames.the_aether.blocks.decorative.BlockAmbrosiumTorch",
        "com.gildedgames.the_aether.blocks.decorative.BlockInfernoTorch",
        "com.gildedgames.the_aether.blocks.BlockEtherealTorch" },
    remap = false)
public abstract class MixinAetherTorchLook extends BlockTorch {

    @Override
    public void registerBlockIcons(IIconRegister register) {
        Torches.Kind kind = Torches.litKind(this);
        if (kind == null) super.registerBlockIcons(register);
        else blockIcon = PaintedSprite
            .register(register, AdventuraArcanaCore.MODID + ":" + kind.id(), source -> TorchPaint.lit(source, kind));
    }
}
