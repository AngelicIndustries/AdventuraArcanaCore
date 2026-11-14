package industries.angelic.adventuraarcanacore.mixins.late.wildmobs;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

/**
 * Wild Mobs' jellyfish and sea scorpion buckets release their creature into the liquid the player aims at, but read
 * the aim without checking for a miss, so using one with no block in reach crashed the game. A miss now leaves the
 * bucket as it is.
 */
@Pseudo
@Mixin(
    targets = { "com.wildmobsmod.items.ItemJellyfishBucket", "com.wildmobsmod.items.ItemSeaScorpionBucket" },
    remap = false)
public abstract class MixinCreatureBucketAim {

    // The targets are not on the compile classpath: name their MCP and shipped SRG methods. Each calls the ray trace
    // through its own class, so the target leaves the owner out.
    @ModifyExpressionValue(
        method = { "onItemRightClick", "func_77659_a" },
        at = @At(
            value = "INVOKE",
            target = "func_77621_a(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Z)Lnet/minecraft/util/MovingObjectPosition;"),
        remap = false,
        require = 1)
    private MovingObjectPosition adventuraarcanacore$missed(MovingObjectPosition aim) {
        return aim != null ? aim : new MovingObjectPosition(0, 0, 0, -1, Vec3.createVectorHelper(0, 0, 0), false);
    }
}
