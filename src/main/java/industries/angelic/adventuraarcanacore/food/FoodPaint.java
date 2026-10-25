package industries.angelic.adventuraarcanacore.food;

import static industries.angelic.adventuraarcanacore.util.SpritePixels.SIZE;
import static industries.angelic.adventuraarcanacore.util.SpritePixels.clamp;
import static industries.angelic.adventuraarcanacore.util.SpritePixels.hsb;
import static industries.angelic.adventuraarcanacore.util.SpritePixels.luminance;
import static industries.angelic.adventuraarcanacore.util.SpritePixels.mix;
import static industries.angelic.adventuraarcanacore.util.SpritePixels.multiply;
import static industries.angelic.adventuraarcanacore.util.SpritePixels.scale;

import java.awt.Color;

import industries.angelic.adventuraarcanacore.util.SpritePixels.Source;

/**
 * The pixels of the food blocks, painted from installed textures when the atlas is stitched (util.PaintedSprite):
 * PMP's berry bush with its berries in another colour, HarvestCraft's fruit in another colour, vanilla's vine with
 * grapes or kiwis. All images are 16x16 ARGB.
 */
public final class FoodPaint {

    private FoodPaint() {}

    /** PMP's bare berry bush. */
    public static int[] bareBush(Source source, String crop) {
        int[] bare = source.read("plantmegapack", "textures/blocks/berrybushBare.png");
        return bare != null ? bare : source.read("harvestcraft", "textures/blocks/" + crop + "crop_2.png");
    }

    /** PMP's ripe blackberry bush with its berries in {@code colour}; HarvestCraft's own crop without PMP. */
    public static int[] bush(Source source, String crop, int colour) {
        int[] bare = source.read("plantmegapack", "textures/blocks/berrybushBare.png");
        int[] ripe = source.read("plantmegapack", "textures/blocks/berrybushBlack4.png");
        if (bare == null || ripe == null) return source.read("harvestcraft", "textures/blocks/" + crop + "crop_3.png");
        int[] out = bare.clone();
        double mean = 0;
        int berries = 0;
        for (int i = 0; i < out.length; i++) if (berry(bare[i], ripe[i])) {
            mean += luminance(ripe[i]);
            berries++;
        }
        if (berries == 0) return out;
        mean /= berries;
        for (int i = 0; i < out.length; i++) if (berry(bare[i], ripe[i]))
            out[i] = scale(colour, clamp(0.85 + 1.2 * (luminance(ripe[i]) - mean) / 255.0, 0.55, 1.35));
        return out;
    }

    /**
     * HarvestCraft's fruit texture {@code fruit} at {@code stage} (0 to 2), its fruit turned towards {@code colour}
     * as it ripens; the green stem on top stays.
     */
    public static int[] pod(Source source, String fruit, int stage, int colour) {
        int[] pixels = source.read("harvestcraft", "textures/blocks/" + fruit + "_" + stage + ".png");
        if (pixels == null) return null;
        double amount = stage / 2.0;
        if (amount == 0) return pixels;
        float[] target = hsb(colour);
        int[] out = pixels.clone();
        for (int i = 0; i < out.length; i++) {
            int argb = out[i];
            if (argb >>> 24 == 0) continue;
            float[] hsb = hsb(argb);
            boolean stem = hsb[0] >= 70 / 360f && hsb[0] <= 170 / 360f && hsb[1] > 0.25f && i / SIZE < 4;
            if (stem) continue;
            int recoloured = Color.HSBtoRGB(target[0], target[1], (float) Math.min(1, hsb[2] * target[2] / 0.75));
            out[i] = argb & 0xFF000000 | mix(argb, recoloured, amount) & 0xFFFFFF;
        }
        return out;
    }

    /** Vanilla's vine in a fixed leaf green, with bunches of {@code fruit} (grape, kiwi) or bare (null). */
    public static int[] vine(Source source, String fruit) {
        int[] vine = source.read("minecraft", "textures/blocks/vine.png");
        if (vine == null) return null;
        int[] out = new int[vine.length];
        for (int i = 0; i < vine.length; i++)
            out[i] = vine[i] >>> 24 == 0 ? 0 : vine[i] & 0xFF000000 | multiply(vine[i], 0x48B518) & 0xFFFFFF;
        if ("grape".equals(fruit)) {
            for (int[] at : new int[][] { { 2, 2 }, { 9, 7 }, { 4, 10 } }) stamp(out, at[0], at[1], GRAPES);
        } else if ("kiwi".equals(fruit)) {
            for (int[] at : new int[][] { { 3, 3 }, { 10, 6 }, { 6, 11 }, { 11, 12 } }) stamp(out, at[0], at[1], KIWI);
        }
        return out;
    }

    private static final int[][] GRAPES = { { 0, 0xFF6E3A7E, 0xFF5A2B68, 0 },
        { 0xFF6E3A7E, 0xFF4B2257, 0xFF5A2B68, 0xFF4B2257 }, { 0xFF5A2B68, 0xFF4B2257, 0xFF6E3A7E, 0xFF3B1846 },
        { 0, 0xFF4B2257, 0xFF3B1846, 0 }, { 0, 0, 0xFF3B1846, 0 } };
    private static final int[][] KIWI = { { 0xFF9C7A45, 0xFF8B6B3D, 0 }, { 0xFF8B6B3D, 0xFF7A5A2E, 0xFF6A4C24 },
        { 0xFF7A5A2E, 0xFF6A4C24, 0xFF5A3F1D }, { 0, 0xFF5A3F1D, 0 } };

    private static void stamp(int[] out, int x0, int y0, int[][] shape) {
        for (int y = 0; y < shape.length; y++) for (int x = 0; x < shape[y].length; x++) {
            int px = x0 + x, py = y0 + y;
            if (shape[y][x] != 0 && px < SIZE && py < SIZE) out[py * SIZE + px] = shape[y][x];
        }
    }

    private static boolean berry(int bare, int ripe) {
        return ripe >>> 24 != 0 && ripe != bare;
    }
}
