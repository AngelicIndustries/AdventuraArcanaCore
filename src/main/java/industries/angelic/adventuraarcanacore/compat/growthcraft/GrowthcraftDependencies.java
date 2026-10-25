package industries.angelic.adventuraarcanacore.compat.growthcraft;

import java.util.regex.Pattern;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public final class GrowthcraftDependencies {

    private static final Pattern FORESTRY_REQUIREMENT = Pattern
        .compile("(^|;)(\\s*)required-after:Forestry(?=@|\\s*(?:;|$))");

    private GrowthcraftDependencies() {}

    public static String makeForestryOptional(String modId, String dependencies) {
        if (!"Growthcraft".equals(modId) || dependencies == null) return dependencies;
        return FORESTRY_REQUIREMENT.matcher(dependencies)
            .replaceAll("$1$2after:Forestry");
    }

    public static boolean makeForestryOptional(String modId, ModMetadata metadata, String annotationRules) {
        if (!"Growthcraft".equals(modId)) return false;
        boolean changed = false;
        for (ArtifactVersion required : metadata.requiredMods) {
            if ("Forestry".equals(required.getLabel())) changed |= addForestryOrder(metadata, required);
        }
        changed |= metadata.requiredMods.removeIf(required -> "Forestry".equals(required.getLabel()));

        // Some releases use mcmod.info, which omits the annotation's Forestry load order.
        ModMetadata annotation = new ModMetadata();
        Loader.instance()
            .computeDependencies(
                makeForestryOptional(modId, annotationRules),
                annotation.requiredMods,
                annotation.dependencies,
                annotation.dependants);
        for (ArtifactVersion dependency : annotation.dependencies) {
            if ("Forestry".equals(dependency.getLabel())) changed |= addForestryOrder(metadata, dependency);
        }
        return changed;
    }

    private static boolean addForestryOrder(ModMetadata metadata, ArtifactVersion forestry) {
        if (metadata.dependencies.stream()
            .anyMatch(dependency -> "Forestry".equals(dependency.getLabel()))) return false;
        metadata.dependencies.add(forestry);
        return true;
    }
}
