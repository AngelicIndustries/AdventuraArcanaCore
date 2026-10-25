package industries.angelic.adventuraarcanacore.food;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FoodPaintTest {

    private static final int LEAF = 0xFF2F6A1E, BERRY = 0xFF201020;

    private static industries.angelic.adventuraarcanacore.util.SpritePixels.Source source(Map<String, int[]> textures) {
        return (domain, path) -> textures.get(domain + ":" + path);
    }

    private static int[] filled(int argb) {
        int[] pixels = new int[industries.angelic.adventuraarcanacore.util.SpritePixels.SIZE
            * industries.angelic.adventuraarcanacore.util.SpritePixels.SIZE];
        java.util.Arrays.fill(pixels, argb);
        return pixels;
    }

    @Test
    public void bushBerriesTakeTheSpeciesColourAndLeavesStay() {
        int[] bare = filled(LEAF), ripe = bare.clone();
        ripe[17] = BERRY;
        ripe[40] = BERRY;
        Map<String, int[]> textures = new HashMap<>();
        textures.put("plantmegapack:textures/blocks/berrybushBare.png", bare);
        textures.put("plantmegapack:textures/blocks/berrybushBlack4.png", ripe);
        int[] cranberry = FoodPaint.bush(source(textures), "cranberry", 0xA5122A);
        assertEquals(LEAF, cranberry[0]);
        assertNotEquals(BERRY, cranberry[17]);
        assertEquals(0xFF, cranberry[17] >>> 24);
        // Same luminance, so the colour itself (scaled by 0.85).
        assertEquals((int) (0xA5 * 0.85), cranberry[17] >> 16 & 255);
    }

    @Test
    public void withoutPmpTheBushIsHarvestCraftsOwnCrop() {
        Map<String, int[]> textures = new HashMap<>();
        int[] crop = filled(0xFF00FF00);
        textures.put("harvestcraft:textures/blocks/raspberrycrop_3.png", crop);
        assertArrayEquals(crop, FoodPaint.bush(source(textures), "raspberry", 0xD6274D));
    }

    @Test
    public void unripePodsKeepHarvestCraftsColours() {
        Map<String, int[]> textures = new HashMap<>();
        int[] date = filled(0xFFC040A0);
        textures.put("harvestcraft:textures/blocks/date_0.png", date);
        textures.put("harvestcraft:textures/blocks/date_2.png", date);
        assertArrayEquals(date, FoodPaint.pod(source(textures), "date", 0, 0x9B1B1F));
        int ripe = FoodPaint.pod(source(textures), "date", 2, 0x9B1B1F)[200];
        assertEquals(0xFF, ripe >>> 24);
        assertNotEquals(date[200], ripe);
        assertNull(FoodPaint.pod(source(textures), "date", 1, 0x9B1B1F));
    }

    @Test
    public void grapeVinesCarryBunches() {
        Map<String, int[]> textures = new HashMap<>();
        textures.put("minecraft:textures/blocks/vine.png", filled(0xFF808080));
        int[] bare = FoodPaint.vine(source(textures), null), grapes = FoodPaint.vine(source(textures), "grape");
        assertEquals(0xFF245A0C, bare[0]);
        int differing = 0;
        for (int i = 0; i < bare.length; i++) if (bare[i] != grapes[i]) differing++;
        assertEquals(3 * 13, differing);
    }
}
