package industries.angelic.adventuraarcanacore.mixins.late.angelica;

import java.nio.file.Path;
import java.util.Map;

import net.coderbot.iris.shaderpack.ProgramSet;
import net.coderbot.iris.shaderpack.ShaderPack;
import net.coderbot.iris.shaderpack.StringPair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.adventuraarcanacore.compat.angelica.DimensionShaderOptions;

/** Dimensions with shader options of their own get their programs from a copy of the pack (DimensionShaderOptions). */
@Mixin(value = ShaderPack.class, remap = false)
public abstract class MixinShaderPackDimensionOptions {

    @Inject(
        method = "<init>(Ljava/nio/file/Path;Ljava/util/Map;Ljava/lang/Iterable;)V",
        at = @At("RETURN"),
        require = 1)
    private void adventuraarcanacore$remember(Path root, Map<String, String> options,
        Iterable<StringPair> environmentDefines, CallbackInfo ci) {
        DimensionShaderOptions.remember((ShaderPack) (Object) this, root, options, environmentDefines);
    }

    @Inject(method = "getProgramSet", at = @At("HEAD"), cancellable = true, require = 1)
    private void adventuraarcanacore$ownOptions(String dimensionName, CallbackInfoReturnable<ProgramSet> cir) {
        ProgramSet own = DimensionShaderOptions.programSet((ShaderPack) (Object) this, dimensionName);
        if (own != null) cir.setReturnValue(own);
    }
}
