package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ToastPaintTest {

    @Test
    public void recolouringKeepsTransparencyAndDoesNotChangeTheSource() {
        int[] original = { 0, 0xFFD8A64C, 0x803E2410, 0xFF8E5B20 };
        int[] toast = original.clone();
        int[] bread = { 0xFF557B55, 0xFF334B33, 0 };
        int[] result = ToastPaint
            .paint((domain, path) -> domain.equals("harvestcraft") ? toast : bread, Toasts.Kind.ABYSSAL);
        assertArrayEquals(original, toast);
        for (int i = 0; i < toast.length; i++) assertEquals(toast[i] >>> 24, result[i] >>> 24);
        assertNotEquals(toast[1], result[1]);
        assertNotEquals(result[1], result[3]);
    }

    @Test
    public void absentBreadTextureFallsBackToPamsToast() {
        int[] toast = { 0xFFD8A64C, 0, 0xFF3E2410 };
        assertArrayEquals(
            toast,
            ToastPaint.paint((domain, path) -> domain.equals("harvestcraft") ? toast : null, Toasts.Kind.CORN));
        assertNull(ToastPaint.paint((domain, path) -> null, Toasts.Kind.CORN));
    }

    @Test
    public void saltedToastCopiesSaltParticlesAndKeepsTheOriginalToast() {
        int[] toast = new int[256];
        toast[100] = 0xFFE8C38A;
        toast[200] = 0xFF3E2410;
        int[] original = toast.clone();
        int[] saltedBread = new int[256];
        java.util.Arrays.fill(saltedBread, 0xFF75571B);
        int[] expected = toast.clone();
        int[] particles = { 18, 36, 37, 49, 50, 52, 82 };
        for (int pixel : particles) {
            saltedBread[pixel] = pixel % 2 == 0 ? 0xFFCEDFCD : 0xFFFFFFFF;
            expected[pixel] = saltedBread[pixel];
        }
        int[] salted = ToastPaint
            .paint((domain, path) -> domain.equals("harvestcraft") ? toast : saltedBread, Toasts.Kind.SALTED);
        assertArrayEquals(expected, salted);
        assertArrayEquals(original, toast);
        assertArrayEquals(
            toast,
            ToastPaint.paint((domain, path) -> domain.equals("harvestcraft") ? toast : null, Toasts.Kind.SALTED));
    }

    @Test
    public void toasterBreadDropsOnlyTheSaltParticles() {
        int[] saltedBread = new int[256];
        java.util.Arrays.fill(saltedBread, 0xFF75571B);
        int[] original = saltedBread.clone();
        int[] expected = saltedBread.clone();
        for (int pixel : new int[] { 18, 36, 37, 49, 50, 52, 82 }) expected[pixel] = 0;
        assertArrayEquals(
            expected,
            ToastPaint.saltlessBread((domain, path) -> domain.equals("saltymod") ? saltedBread : null));
        assertArrayEquals(original, saltedBread);
        assertNull(ToastPaint.saltlessBread((domain, path) -> null));
    }
}
