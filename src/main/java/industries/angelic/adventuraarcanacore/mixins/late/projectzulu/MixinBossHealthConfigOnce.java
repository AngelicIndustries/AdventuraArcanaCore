package industries.angelic.adventuraarcanacore.mixins.late.projectzulu;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Project Zulu's boss health bar read its one setting by loading ProjectZuluConfig.cfg and writing it back on every
 * render tick, twice a frame. The file is now read once and written only the first time, which still adds the
 * setting when it's missing.
 */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.mobs.BossHealthDisplayTicker", remap = false)
public abstract class MixinBossHealthConfigOnce {

    @Unique
    private Configuration adventuraarcanacore$config;
    @Unique
    private boolean adventuraarcanacore$saved;

    @Redirect(
        method = "TickEvent",
        at = @At(value = "NEW", target = "(Ljava/io/File;)Lnet/minecraftforge/common/config/Configuration;"),
        remap = false,
        require = 1)
    private Configuration adventuraarcanacore$readOnce(File file) {
        if (adventuraarcanacore$config == null) {
            adventuraarcanacore$config = new Configuration(file);
            adventuraarcanacore$config.load();
        }
        return adventuraarcanacore$config;
    }

    @Redirect(
        method = "TickEvent",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/config/Configuration;load()V"),
        remap = false,
        require = 1)
    private void adventuraarcanacore$alreadyLoaded(Configuration config) {}

    @Redirect(
        method = "TickEvent",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/config/Configuration;save()V"),
        remap = false,
        require = 1)
    private void adventuraarcanacore$saveOnce(Configuration config) {
        if (adventuraarcanacore$saved) return;
        adventuraarcanacore$saved = true;
        config.save();
    }
}
