package industries.angelic.adventuraarcanacore.mixins.early.fml;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModMetadata;
import industries.angelic.adventuraarcanacore.compat.growthcraft.GrowthcraftDependencies;

@Mixin(value = FMLModContainer.class, remap = false)
public abstract class MixinFMLModContainer {

    @Shadow
    private Map<String, Object> descriptor;

    @Shadow
    private ModMetadata modMetadata;

    @Shadow
    private String annotationDependencies;

    @Inject(method = "bindMetadata", at = @At("RETURN"))
    private void adventuraarcanacore$optionalForestry(MetadataCollection metadata, CallbackInfo ci) {
        String modId = (String) descriptor.get("modid");
        if (!"Growthcraft".equals(modId)) return;
        annotationDependencies = GrowthcraftDependencies.makeForestryOptional(modId, annotationDependencies);
        // Dependency validation happens after metadata binding, regardless of the metadata source.
        if (GrowthcraftDependencies.makeForestryOptional(modId, modMetadata, (String) descriptor.get("dependencies"))) {
            FMLLog.info("Adventura Arcana Core: made Growthcraft's Forestry dependency optional");
        }
    }
}
