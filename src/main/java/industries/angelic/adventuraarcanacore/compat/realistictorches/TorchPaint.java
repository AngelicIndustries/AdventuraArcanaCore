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

    /**
     * A lit torch's look: its texture, or with another torch's flame colours on its own flame, each pixel taking the
     * colour at its place in the light-to-dark order (the Aether's flames on vanilla's torch and Netherlicious' bone
     * torch).
     */
    public static int[] lit(SpritePixels.Source source, Torches.Kind kind) {
        int[] base = block(source, kind.look);
        if (base == null || kind.flameFrom == null) return base;
        int[] other = block(source, kind.flameFrom);
        if (other == null) return null;
        // The other torch's flame: what it has above where this one's stick starts.
        List<Integer> palette = new ArrayList<>();
        for (int i = 0; i < (topRow(base) + 2) * SIZE; i++) if (other[i] >>> 24 != 0) palette.add(other[i]);
        int[] out = base.clone();
        recolor(out, flame(base), palette);
        return out;
    }

    /** The lit torch with its flame faded and dimmed, as Realistic Torches' smoldering torch; fire also reddens. */
    static int[] smolder(SpritePixels.Source source, Torches.Kind kind) {
        int[] lit = lit(source, kind);
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

    /**
     * The Aether's Zanite and Cinerarium with its cinerarium in ambrosium's colours: its reds take the ambrosium
     * shard's yellows by the light-to-dark order; the zanite stays.
     */
    static int[] zaniteAndAmbrosium(SpritePixels.Source source) {
        int[] lighter = source.read("aether_legacy", "textures/items/tools/zanite_and_cinerarium.png");
        int[] shard = source.read("aether_legacy", "textures/items/misc/ambrosium_shard.png");
        if (lighter == null || shard == null) return lighter;
        List<Integer> reds = new ArrayList<>(), yellows = new ArrayList<>();
        for (int i = 0; i < lighter.length; i++) {
            float[] hsb = SpritePixels.hsb(lighter[i]);
            if (lighter[i] >>> 24 != 0 && hsb[1] > 0.3F && (hsb[0] < 0.15F || hsb[0] > 0.85F)) reds.add(i);
        }
        // Without its outline and shine.
        for (int pixel : shard) {
            float[] hsb = SpritePixels.hsb(pixel);
            if (pixel >>> 24 != 0 && hsb[1] >= 0.3F && hsb[2] >= 0.4F) yellows.add(pixel);
        }
        int[] out = lighter.clone();
        recolor(
            out,
            reds.stream()
                .mapToInt(Integer::intValue)
                .toArray(),
            yellows);
        return out;
    }

    /**
     * Gives the cells' colours those of the palette at the same place in the light-to-dark order, colour by colour,
     * keeping each cell's alpha.
     */
    private static void recolor(int[] pixels, int[] cells, List<Integer> palette) {
        List<Integer> from = new ArrayList<>(), to = new ArrayList<>();
        for (int cell : cells) if (!from.contains(pixels[cell] & 0xFFFFFF)) from.add(pixels[cell] & 0xFFFFFF);
        for (int colour : palette) if (!to.contains(colour & 0xFFFFFF)) to.add(colour & 0xFFFFFF);
        if (from.isEmpty() || to.isEmpty()) return;
        from.sort(Comparator.comparingDouble(SpritePixels::luminance));
        to.sort(Comparator.comparingDouble(SpritePixels::luminance));
        for (int cell : cells) {
            int rank = from.indexOf(pixels[cell] & 0xFFFFFF);
            int index = from.size() == 1 ? to.size() - 1
                : (int) Math.round((double) rank * (to.size() - 1) / (from.size() - 1));
            pixels[cell] = pixels[cell] & 0xFF000000 | to.get(index);
        }
    }

    /** A block texture, by domain:name. */
    private static int[] block(SpritePixels.Source source, String texture) {
        int colon = texture.indexOf(':');
        return source.read(texture.substring(0, colon), "textures/blocks/" + texture.substring(colon + 1) + ".png");
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
