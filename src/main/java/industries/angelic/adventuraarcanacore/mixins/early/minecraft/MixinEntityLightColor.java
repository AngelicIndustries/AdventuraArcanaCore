package industries.angelic.adventuraarcanacore.mixins.early.minecraft;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.gtnewhorizons.angelica.dynamiclights.IDynamicLightSource;

import industries.angelic.adventuraarcanacore.compat.angelica.DynamicLightColor;

/**
 * Gives Angelica's dynamic lights a color: Angelica makes Entity a light source and leaves its RGB white (the
 * interface's default); this overrides it with DynamicLightColor's. Applied after Angelica (higher priority), whose
 * mixin adds the interface. Worked out at most once a tick, or when the brightness changes, as the chunk builders ask
 * for it per corner.
 */
@Mixin(value = Entity.class, priority = 1100)
public abstract class MixinEntityLightColor {

    @Shadow
    public int ticksExisted;

    @Unique
    private int adventuraarcanacore$rgbTick = Integer.MIN_VALUE, adventuraarcanacore$rgbLuminance = -1,
        adventuraarcanacore$rgb;

    public int angelica$getLuminanceRGB() {
        int luminance = ((IDynamicLightSource) this).angelica$getLuminance();
        if (luminance <= 0) return 0;
        if (adventuraarcanacore$rgbTick != ticksExisted || adventuraarcanacore$rgbLuminance != luminance) {
            adventuraarcanacore$rgb = DynamicLightColor.rgb((Entity) (Object) this, luminance);
            adventuraarcanacore$rgbLuminance = luminance;
            adventuraarcanacore$rgbTick = ticksExisted;
        }
        return adventuraarcanacore$rgb;
    }
}
