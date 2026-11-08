package industries.angelic.adventuraarcanacore.mixins.late.angelica;

import java.util.Locale;

import org.embeddedt.embeddium.api.util.ColorABGR;
import org.embeddedt.embeddium.impl.model.light.data.QuadLightData;
import org.embeddedt.embeddium.impl.render.chunk.vertex.format.ChunkVertexEncoder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.angelica.api.ExtQuadLightData;
import com.gtnewhorizons.angelica.api.TintRegistry;
import com.gtnewhorizons.angelica.rendering.celeritas.AngelicaChunkBuildContext;

import industries.angelic.adventuraarcanacore.compat.angelica.ColoredLightDebug;

/**
 * Reports what Angelica's colored tint does to the quads of the block /lightdebug traces: whether it has colored light,
 * the per-corner block and sky light it tints with (sky after the time of day), the tint, and the vertex colors before
 * and after. One build context per chunk build thread, so the captured colors need no locking.
 */
@Mixin(value = AngelicaChunkBuildContext.class, remap = false)
public abstract class MixinChunkBuildTintDebug {

    @Shadow
    @Final
    private QuadLightData quadLightData;
    @Shadow
    @Final
    private boolean hasColoredLight;
    @Shadow
    private float cachedSkylightSubtracted;

    @Unique
    private int[] adventuraarcanacore$colorsBefore;

    @Inject(method = "applyBlockLightTint", at = @At("HEAD"), require = 1)
    private void adventuraarcanacore$traceInputs(int x, int y, int z, ChunkVertexEncoder.Vertex[] vertices,
        CallbackInfo ci) {
        adventuraarcanacore$colorsBefore = null;
        if (!ColoredLightDebug.trace(x, y, z)) return;
        ExtQuadLightData light = (ExtQuadLightData) quadLightData;
        boolean rgb = light.angelica$isRGBValid(), sky = light.angelica$isSkyRGBValid();
        int[] before = new int[4];
        float[] sum = new float[9];
        float[] tint = new float[3];
        for (int i = 0; i < 4; i++) {
            before[i] = vertices[i].color;
            float r = rgb ? light.angelica$getR()[i] : 0, g = rgb ? light.angelica$getG()[i] : 0,
                b = rgb ? light.angelica$getB()[i] : 0;
            float sr = sky ? light.angelica$getSkyR()[i] : 0, sg = sky ? light.angelica$getSkyG()[i] : 0,
                sb = sky ? light.angelica$getSkyB()[i] : 0;
            float er = Math.max(0, sr - cachedSkylightSubtracted), eg = Math.max(0, sg - cachedSkylightSubtracted),
                eb = Math.max(0, sb - cachedSkylightSubtracted);
            TintRegistry.getCurrent()
                .computeTint(r, g, b, er, eg, eb, tint);
            ColoredLightDebug.log(
                String.format(
                    Locale.ROOT,
                    "corner %d at %.2f %.2f %.2f: block (%.1f %.1f %.1f) sky (%.1f %.1f %.1f) -> (%.1f %.1f %.1f)"
                        + " tint (%.2f %.2f %.2f) lightmap block %d sky %d",
                    i,
                    vertices[i].x,
                    vertices[i].y,
                    vertices[i].z,
                    r,
                    g,
                    b,
                    sr,
                    sg,
                    sb,
                    er,
                    eg,
                    eb,
                    tint[0],
                    tint[1],
                    tint[2],
                    vertices[i].light & 0xFFFF,
                    vertices[i].light >>> 16));
            float[] corner = { r, g, b, er, eg, eb, tint[0], tint[1], tint[2] };
            for (int k = 0; k < 9; k++) sum[k] += corner[k] / 4;
        }
        ColoredLightDebug.say(
            String.format(
                Locale.ROOT,
                "face %s: colored %s, rgb %s, sky %s; block (%.1f %.1f %.1f) sky (%.1f %.1f %.1f) after time %.0f;"
                    + " tint (%.2f %.2f %.2f)",
                adventuraarcanacore$face(vertices),
                hasColoredLight,
                rgb ? "valid" : "INVALID",
                sky ? "valid" : "INVALID",
                sum[0],
                sum[1],
                sum[2],
                sum[3],
                sum[4],
                sum[5],
                cachedSkylightSubtracted,
                sum[6],
                sum[7],
                sum[8]));
        adventuraarcanacore$colorsBefore = before;
    }

    @Inject(method = "applyBlockLightTint", at = @At("RETURN"), require = 1)
    private void adventuraarcanacore$traceOutput(int x, int y, int z, ChunkVertexEncoder.Vertex[] vertices,
        CallbackInfo ci) {
        int[] before = adventuraarcanacore$colorsBefore;
        if (before == null) return;
        adventuraarcanacore$colorsBefore = null;
        StringBuilder colors = new StringBuilder("  vertex colors");
        for (int i = 0; i < 4; i++) colors.append(' ')
            .append(adventuraarcanacore$rgb(before[i]))
            .append("->")
            .append(adventuraarcanacore$rgb(vertices[i].color));
        ColoredLightDebug.say(colors.toString());
    }

    /** The axis all four corners share, which names the face. */
    @Unique
    private static String adventuraarcanacore$face(ChunkVertexEncoder.Vertex[] vertices) {
        float[][] axes = new float[3][4];
        for (int i = 0; i < 4; i++) {
            axes[0][i] = vertices[i].x;
            axes[1][i] = vertices[i].y;
            axes[2][i] = vertices[i].z;
        }
        for (int a = 0; a < 3; a++) {
            float min = Float.MAX_VALUE, max = -Float.MAX_VALUE;
            for (float v : axes[a]) {
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
            if (max - min < 1e-4f) return "xyz".charAt(a) + "=" + String.format(Locale.ROOT, "%.2f", min);
        }
        return "slanted";
    }

    @Unique
    private static String adventuraarcanacore$rgb(int abgr) {
        return String.format(
            Locale.ROOT,
            "#%02x%02x%02x",
            ColorABGR.unpackRed(abgr),
            ColorABGR.unpackGreen(abgr),
            ColorABGR.unpackBlue(abgr));
    }
}
