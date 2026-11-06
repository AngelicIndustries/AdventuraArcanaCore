package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.entity.item.EntityMinecartTNT;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Blows up a TNT minecart (see MixinMinecartTNTBurningArrow). */
@Mixin(EntityMinecartTNT.class)
public interface AccessorMinecartTNT {

    @Invoker("explodeCart")
    void adventuraarcanacore$explode(double speedSquared);
}
