package industries.angelic.adventuraarcanacore.compat.aether;

import java.lang.reflect.Field;

import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * The Aether reads whether a player is jumping by reflection every tick, trying the development name first: outside
 * development that name never exists, so every read created an exception. The field is found once instead.
 */
public final class AetherJumping {

    private static volatile Field field;
    private static volatile Class<?> fieldOwner;

    private AetherJumping() {}

    /** What {@link ReflectionHelper#getPrivateValue(Class, Object, String...)} gives, from a field found once. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object read(Class type, Object instance, String[] names) {
        Field known = field;
        if (known == null || fieldOwner != type) {
            known = ReflectionHelper.findField(type, names);
            field = known;
            fieldOwner = type;
        }
        try {
            return known.get(instance);
        } catch (IllegalAccessException e) {
            return ReflectionHelper.getPrivateValue(type, instance, names);
        }
    }
}
