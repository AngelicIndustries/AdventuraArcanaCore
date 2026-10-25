package industries.angelic.adventuraarcanacore.compat.growthcraft;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import industries.angelic.adventuraarcanacore.compartment.ForgeTestRunner;

@RunWith(ForgeTestRunner.class)
public class GrowthcraftDependenciesTest {

    private static final String ORIGINAL = "required-after:Forge@[10.13.4.1566,);required-after:AppleCore@[1.3.0,);required-after:Forestry@[4.2.8,)";

    @BeforeClass
    public static void bootstrap() {
        Loader.injectData("7", "99", "40", "1614", "1.7.10", "9.05", new File("."), Collections.emptyList());
    }

    @Test
    public void onlyForestryBecomesOptional() {
        assertEquals(
            "required-after:Forge@[10.13.4.1566,);required-after:AppleCore@[1.3.0,);after:Forestry@[4.2.8,)",
            GrowthcraftDependencies.makeForestryOptional("Growthcraft", ORIGINAL));
    }

    @Test
    public void unrelatedModsAndSimilarIdsStayUnchanged() {
        assertEquals(ORIGINAL, GrowthcraftDependencies.makeForestryOptional("AnotherMod", ORIGINAL));
        assertEquals(ORIGINAL, GrowthcraftDependencies.makeForestryOptional("Growthcraft|Bees", ORIGINAL));
        assertEquals(ORIGINAL, GrowthcraftDependencies.makeForestryOptional("growthcraft", ORIGINAL));
        String rules = "required-after:ForestryExtras;required-after:SomeForestry;before:Forestry";
        assertEquals(rules, GrowthcraftDependencies.makeForestryOptional("Growthcraft", rules));
    }

    @Test
    public void supportsVersionedAndUnversionedEntriesAtAnyPosition() {
        assertEquals(
            "after:Forestry",
            GrowthcraftDependencies.makeForestryOptional("Growthcraft", "required-after:Forestry"));
        assertEquals(
            "after:Forestry@[4,5);after:Other",
            GrowthcraftDependencies.makeForestryOptional("Growthcraft", "required-after:Forestry@[4,5);after:Other"));
        assertEquals(
            "after:Other; after:Forestry ;before:Last",
            GrowthcraftDependencies
                .makeForestryOptional("Growthcraft", "after:Other; required-after:Forestry ;before:Last"));
    }

    @Test
    public void absentAndAlreadyOptionalRulesStayUnchanged() {
        assertNull(GrowthcraftDependencies.makeForestryOptional("Growthcraft", null));
        assertEquals("", GrowthcraftDependencies.makeForestryOptional("Growthcraft", ""));
        String optional = "after:Forestry@[4.2.8,);required-after:AppleCore@[1.3.0,)";
        assertEquals(optional, GrowthcraftDependencies.makeForestryOptional("Growthcraft", optional));
        String patched = GrowthcraftDependencies.makeForestryOptional("Growthcraft", ORIGINAL);
        assertEquals(patched, GrowthcraftDependencies.makeForestryOptional("Growthcraft", patched));
    }

    @Test
    public void forgeKeepsLoadOrderAndVersionChecksWithoutRequiringForestry() {
        Set<ArtifactVersion> required = new HashSet<>();
        List<ArtifactVersion> after = new ArrayList<>();
        List<ArtifactVersion> before = new ArrayList<>();
        Loader.instance()
            .computeDependencies(
                GrowthcraftDependencies.makeForestryOptional("Growthcraft", ORIGINAL),
                required,
                after,
                before);
        assertEquals(2, required.size());
        assertTrue(
            required.stream()
                .anyMatch(version -> "Forge".equals(version.getLabel())));
        assertTrue(
            required.stream()
                .anyMatch(version -> "AppleCore".equals(version.getLabel())));
        assertFalse(
            required.stream()
                .anyMatch(version -> "Forestry".equals(version.getLabel())));
        assertEquals(3, after.size());
        ArtifactVersion forestry = after.stream()
            .filter(version -> "Forestry".equals(version.getLabel()))
            .findFirst()
            .get();
        assertTrue(forestry.containsVersion(new DefaultArtifactVersion("Forestry", "4.2.8")));
        assertFalse(forestry.containsVersion(new DefaultArtifactVersion("Forestry", "4.2.7")));
        assertTrue(before.isEmpty());
    }

    @Test
    public void annotationMetadataLosesOnlyTheForestryRequirement() {
        ModMetadata metadata = new ModMetadata();
        Loader.instance()
            .computeDependencies(ORIGINAL, metadata.requiredMods, metadata.dependencies, metadata.dependants);
        List<ArtifactVersion> order = new ArrayList<>(metadata.dependencies);
        assertTrue(GrowthcraftDependencies.makeForestryOptional("Growthcraft", metadata, ORIGINAL));
        assertEquals(2, metadata.requiredMods.size());
        assertFalse(
            metadata.requiredMods.stream()
                .anyMatch(required -> "Forestry".equals(required.getLabel())));
        assertEquals(order, metadata.dependencies);
        assertFalse(GrowthcraftDependencies.makeForestryOptional("Growthcraft", metadata, ORIGINAL));
    }

    @Test
    public void jsonMetadataGainsForestryOrderWithoutChangingOtherRequirements() {
        ModMetadata metadata = new ModMetadata();
        metadata.requiredMods.add(new DefaultArtifactVersion("Forge", true));
        Set<ArtifactVersion> required = new HashSet<>(metadata.requiredMods);
        assertTrue(GrowthcraftDependencies.makeForestryOptional("Growthcraft", metadata, ORIGINAL));
        assertEquals(required, metadata.requiredMods);
        assertEquals(1, metadata.dependencies.size());
        ArtifactVersion forestry = metadata.dependencies.get(0);
        assertEquals("Forestry", forestry.getLabel());
        assertTrue(forestry.containsVersion(new DefaultArtifactVersion("Forestry", "4.2.8")));
        assertFalse(forestry.containsVersion(new DefaultArtifactVersion("Forestry", "4.2.7")));
        assertFalse(GrowthcraftDependencies.makeForestryOptional("Growthcraft", metadata, ORIGINAL));
    }

    @Test
    public void metadataOnlyRequirementsKeepTheirVersionAndOtherModsAreUntouched() {
        ModMetadata metadata = new ModMetadata();
        Loader.instance()
            .computeDependencies(
                "required-after:Forestry@[4,5)",
                metadata.requiredMods,
                metadata.dependencies,
                metadata.dependants);
        assertFalse(GrowthcraftDependencies.makeForestryOptional("AnotherMod", metadata, null));
        assertEquals(1, metadata.requiredMods.size());
        metadata.dependencies.clear();
        assertTrue(GrowthcraftDependencies.makeForestryOptional("Growthcraft", metadata, null));
        assertTrue(metadata.requiredMods.isEmpty());
        assertEquals(1, metadata.dependencies.size());
        assertTrue(
            metadata.dependencies.get(0)
                .containsVersion(new DefaultArtifactVersion("Forestry", "4.5")));
        assertFalse(
            metadata.dependencies.get(0)
                .containsVersion(new DefaultArtifactVersion("Forestry", "5")));
    }
}
