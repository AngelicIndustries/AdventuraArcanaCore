package industries.angelic.adventuraarcanacore.mixins.late.villagenames;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Village Names looks a village's name up by the nearest named village within 112 blocks in 3D. Its Antique Atlas
 * integration names a village from the saved structure before it is built, at vanilla's placeholder height (y 64 to
 * 69), and the well's sign asks again at the village's real height. In Biomic's tall worlds that is 120 to 260 blocks
 * higher, so the sign got a second name, and the atlas marker and waystones kept the first. Compare horizontally:
 * village starts are at least 128 blocks apart, so no two villages share one.
 */
@Pseudo
@Mixin(targets = "astrotibs.villagenames.village.StructureVillageVN", remap = false)
public abstract class MixinVillageNameLookup {

    @Redirect(
        method = "getOrMakeVNInfo",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/NBTTagCompound;getInteger(Ljava/lang/String;)I",
            remap = true),
        require = 1)
    private static int adventuraarcanacore$flatLookup(NBTTagCompound tag, String key, World world, int posX, int posY,
        int posZ) {
        return "signY".equals(key) ? posY : tag.getInteger(key);
    }
}
