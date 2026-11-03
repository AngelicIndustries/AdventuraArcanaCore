package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import java.awt.Color;

import industries.angelic.adventuraarcanacore.util.SpritePixels;

/** Pam's toast with salt flecks or colours from the corresponding bread in the installed resource pack. */
public final class ToastPaint {

    private static final int[] SALT_PIXELS = { 18, 36, 37, 49, 50, 52, 82 };

    private ToastPaint() {}

    public static int[] paint(SpritePixels.Source source, Toasts.Kind kind) {
        int[] toast = source.read("harvestcraft", "textures/items/toastItem.png");
        if (toast == null) return null;
        if (kind == Toasts.Kind.SALTED) {
            int[] out = toast.clone();
            int[] saltedBread = source.read(kind.mod, "textures/items/" + kind.texture + ".png");
            // Copy SaltyMod's top-left salt particles, including those outside the bread outline.
            if (saltedBread != null) for (int pixel : SALT_PIXELS) out[pixel] = saltedBread[pixel];
            return out;
        }
        int[] bread = source.read(kind.mod, "textures/items/" + kind.texture + ".png");
        if (bread == null) return toast.clone();
        double r = 0, g = 0, b = 0;
        int count = 0;
        for (int pixel : bread) if (pixel >>> 24 != 0) {
            r += pixel >> 16 & 255;
            g += pixel >> 8 & 255;
            b += pixel & 255;
            count++;
        }
        if (count == 0) return toast.clone();
        float[] target = SpritePixels.hsb((int) (r / count) << 16 | (int) (g / count) << 8 | (int) (b / count));
        int[] out = toast.clone();
        for (int i = 0; i < out.length; i++) {
            if (out[i] >>> 24 == 0) continue;
            float[] original = SpritePixels.hsb(out[i]);
            float brightness = (float) SpritePixels.clamp(original[2] * (0.55 + target[2]), 0, 1);
            int colour = Color.HSBtoRGB(target[0], (original[1] + target[1]) / 2, brightness);
            out[i] = out[i] & 0xFF000000 | colour & 0xFFFFFF;
        }
        return out;
    }
}
