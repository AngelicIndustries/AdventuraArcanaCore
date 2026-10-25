package industries.angelic.adventuraarcanacore.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

public class PatchSwitchesTest {

    @Test
    public void readsOnlyThePatchesCategory() {
        Map<String, Boolean> values = PatchSwitches.parse(
            Arrays.asList(
                "# Configuration file",
                "general {",
                "    B:noNaturaClouds=false",
                "}",
                "patches {",
                "    # Natura generates no clouds in any dimension.",
                "    B:noNaturaClouds=false",
                "    B:\"saltyModDedicatedServer\"=true",
                "    nested {",
                "        B:growthcraftWithoutForestry=false",
                "    }",
                "}"));
        assertEquals(2, values.size());
        assertFalse(values.get("noNaturaClouds"));
        assertTrue(values.get("saltyModDedicatedServer"));
    }
}
