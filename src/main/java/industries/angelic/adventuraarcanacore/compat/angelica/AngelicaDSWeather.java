package industries.angelic.adventuraarcanacore.compat.angelica;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Runtime side of ANGELICA_DS_WEATHER. {@link #verify} checks every assumption the patch makes about Angelica and
 * Dynamic Surroundings and stops the game at startup when one no longer holds, instead of letting the rain sounds
 * and auroras go quietly missing again.
 */
public final class AngelicaDSWeather {

    /** Added to Angelica's WeatherRenderer by the mixin, so startup can tell that it applied. */
    public interface Patched {
    }

    private static final String WEATHER_RENDERER = "com.gtnewhorizons.angelica.render.WeatherRenderer";
    private static final String WEATHER_HOOK = "com/gtnewhorizons/angelica/mixins/early/angelica/rendering/MixinEntityRenderer_Weather.class";
    private static final String[] HOOK_CALLS = { "render(F)Z", "spawnRainParticles()Z" };
    private static final String RENDER_WEATHER = "org.blockartistry.mod.DynSurround.client.RenderWeather";
    private static final String AURORA_RENDERER = "org.blockartistry.mod.DynSurround.client.aurora.AuroraRenderer";

    private static Object auroraRenderer;
    private static Method auroraRender;

    private AngelicaDSWeather() {}

    public static void verify() {
        // Loading the class applies the mixin; a missing target method crashes here (defaultRequire 1).
        if (!Patched.class.isAssignableFrom(load(WEATHER_RENDERER))) {
            fail(WEATHER_RENDERER + " was loaded without the patch");
        }
        // The patch only matters if Angelica's EntityRenderer hooks still cancel on these two methods.
        Set<String> calls = weatherRendererCalls();
        for (String call : HOOK_CALLS) {
            if (!calls.contains(call)) fail("Angelica's weather hook no longer calls WeatherRenderer." + call);
        }
        try {
            Field list = load(RENDER_WEATHER).getDeclaredField("renderList");
            list.setAccessible(true);
            Class<?> aurora = load(AURORA_RENDERER);
            for (Object renderer : (List<?>) list.get(null)) {
                if (aurora.isInstance(renderer)) auroraRenderer = renderer;
            }
            auroraRender = aurora.getMethod("render", EntityRenderer.class, float.class);
        } catch (ReflectiveOperationException | ClassCastException e) {
            fail("Dynamic Surroundings' weather renderers changed: " + e);
        }
        if (auroraRenderer == null) fail("Dynamic Surroundings no longer registers an AuroraRenderer in RenderWeather");
    }

    public static void renderAuroras(float partialTicks) {
        try {
            auroraRender.invoke(auroraRenderer, Minecraft.getMinecraft().entityRenderer, partialTicks);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Class<?> load(String name) {
        try {
            return Class.forName(name, false, AngelicaDSWeather.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            fail(name + " is gone");
            return null;
        }
    }

    private static Set<String> weatherRendererCalls() {
        Set<String> calls = new HashSet<>();
        try (InputStream in = AngelicaDSWeather.class.getClassLoader()
            .getResourceAsStream(WEATHER_HOOK)) {
            if (in == null) fail("Angelica's weather hook " + WEATHER_HOOK + " is gone");
            new ClassReader(in).accept(new ClassVisitor(Opcodes.ASM5) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                    String[] exceptions) {
                    return new MethodVisitor(Opcodes.ASM5) {

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String method, String methodDesc,
                            boolean itf) {
                            if (owner.equals(WEATHER_RENDERER.replace('.', '/'))) calls.add(method + methodDesc);
                        }
                    };
                }
            }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (IOException e) {
            fail("can't read Angelica's weather hook: " + e);
        }
        return calls;
    }

    private static void fail(String problem) {
        throw new IllegalStateException(
            "Adventura Arcana Core: " + problem
                + ". ANGELICA_DS_WEATHER needs updating for this Angelica or Dynamic Surroundings version,"
                + " or turn off angelicaDsWeather in the patches section of config/adventuraarcanacore.cfg.");
    }
}
