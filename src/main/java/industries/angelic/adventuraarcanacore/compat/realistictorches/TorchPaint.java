package industries.angelic.adventuraarcanacore.compat.realistictorches;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import industries.angelic.adventuraarcanacore.util.SpritePixels;

/**
 * The torches' painted sprites, from the installed textures. A torch's flame is the top two rows of its shape, over the
 * columns of the top one, as in vanilla's torch and every torch here.
 */
public final class TorchPaint {

    private static final int SIZE = SpritePixels.SIZE;

    private TorchPaint() {}

    /** The lit torch with its flame faded and dimmed, as Realistic Torches' smoldering torch; fire also reddens. */
    static int[] smolder(SpritePixels.Source source, Torches.Kind kind) {
        int[] lit = source.read("netherlicious", "textures/blocks/" + kind.texture + ".png");
        if (lit == null) return null;
        int[] out = lit.clone();
        int top = topRow(lit);
        for (int pixel : flame(lit)) {
            float[] hsb = SpritePixels.hsb(lit[pixel]);
            float hue = kind.flame == Torches.Flame.FIRE ? hsb[0] * 0.35F : hsb[0];
            float brightness = pixel / SIZE == top ? hsb[2] * 0.82F : Math.min(1, hsb[2] * 1.02F);
            out[pixel] = lit[pixel] & 0xFF000000 | Color.HSBtoRGB(hue, hsb[1] * 0.4F, brightness) & 0xFFFFFF;
        }
        return out;
    }

    /** Netherlicious' bone torch with the charred tip of Realistic Torches' unlit torch in place of its flame. */
    static int[] unlitBone(SpritePixels.Source source) {
        int[] bone = source.read("netherlicious", "textures/blocks/torch_bone.png");
        int[] charred = source.read("realistictorches", "textures/blocks/TorchUnlit.png");
        if (bone == null || charred == null) return bone;
        int[] out = bone.clone();
        int[] flame = flame(bone), tip = flame(charred);
        if (flame.length != tip.length) return out;
        for (int i = 0; i < flame.length; i++) out[flame[i]] = charred[tip[i]];
        return out;
    }

    /**
     * Vanilla's clay ball in glowstone dust's colours, a quarter clay: each pixel takes the dust's colour at the same
     * place in the light-to-dark order, over its lighter three quarters.
     */
    static int[] paste(SpritePixels.Source source) {
        int[] clay = source.read("minecraft", "textures/items/clay_ball.png");
        int[] dust = source.read("minecraft", "textures/items/glowstone_dust.png");
        if (clay == null || dust == null) return clay;
        List<Integer> palette = new ArrayList<>();
        for (int pixel : dust) if (pixel >>> 24 != 0) palette.add(pixel);
        if (palette.isEmpty()) return clay;
        palette.sort(Comparator.comparingDouble(SpritePixels::luminance));
        double low = Double.MAX_VALUE, high = 0;
        for (int pixel : clay) if (pixel >>> 24 != 0) {
            low = Math.min(low, SpritePixels.luminance(pixel));
            high = Math.max(high, SpritePixels.luminance(pixel));
        }
        int[] out = clay.clone();
        for (int i = 0; i < out.length; i++) {
            if (clay[i] >>> 24 == 0) continue;
            double t = high > low ? (SpritePixels.luminance(clay[i]) - low) / (high - low) : 1;
            int colour = palette.get((int) Math.round((0.3 + 0.7 * t) * (palette.size() - 1)));
            out[i] = clay[i] & 0xFF000000 | SpritePixels.mix(colour, clay[i], 0.25);
        }
        return out;
    }

    private static int topRow(int[] pixels) {
        for (int i = 0; i < pixels.length; i++) if (pixels[i] >>> 24 != 0) return i / SIZE;
        return 0;
    }

    /** The flame's pixels, row by row, left to right. */
    private static int[] flame(int[] pixels) {
        int top = topRow(pixels);
        List<Integer> cells = new ArrayList<>();
        for (int y = top; y <= top + 1 && y < SIZE; y++) for (int x = 0; x < SIZE; x++)
            if (pixels[top * SIZE + x] >>> 24 != 0 && pixels[y * SIZE + x] >>> 24 != 0) cells.add(y * SIZE + x);
        return cells.stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }
}
