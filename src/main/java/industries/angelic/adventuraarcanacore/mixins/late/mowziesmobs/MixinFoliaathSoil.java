package industries.angelic.adventuraarcanacore.mixins.late.mowziesmobs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

/**
 * Mowzie's Mobs plants a Foliaath seed only on vanilla grass, dirt or leaves, so it would not take on the pack's other
 * dirts and grasses (Biomes O' Plenty's, the Aether's, Et Futurum's coarse dirt and more). Any grass or dirt now
 * takes it, farmland aside. Baby Foliaaths only come from seeds, so natural spawning is unchanged.
 */
@Pseudo
@Mixin(targets = "com.bobmowzie.mowziesmobs.server.entity.foliaath.EntityBabyFoliaath", remap = false)
public abstract class MixinFoliaathSoil {

    // The target is not on the compile classpath: name its MCP and shipped SRG methods.
    @ModifyReturnValue(method = { "getCanSpawnHere", "func_70601_bi" }, at = @At("RETURN"), require = 1, remap = false)
    private boolean adventuraarcanacore$packSoil(boolean plantable) {
        if (plantable) return true;
        EntityLiving foliaath = (EntityLiving) (Object) this;
        World world = foliaath.worldObj;
        // The same room checks Mowzie's Mobs makes before looking at the ground.
        if (!world.checkNoEntityCollision(foliaath.boundingBox)
            || !world.getCollidingBoundingBoxes(foliaath, foliaath.boundingBox)
                .isEmpty()
            || world.isAnyLiquid(foliaath.boundingBox)) return false;
        Block ground = world.getBlock(
            MathHelper.floor_double(foliaath.posX),
            MathHelper.floor_double(foliaath.boundingBox.minY) - 1,
            MathHelper.floor_double(foliaath.posZ));
        Material material = ground.getMaterial();
        if (material != Material.grass && material != Material.ground || ground instanceof BlockFarmland) return false;
        // As on vanilla grass.
        foliaath.playSound("dig.grass", 1.0F, 0.8F);
        return true;
    }
}
