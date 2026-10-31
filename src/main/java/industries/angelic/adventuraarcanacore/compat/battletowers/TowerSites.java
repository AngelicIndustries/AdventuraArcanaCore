package industries.angelic.adventuraarcanacore.compat.battletowers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import cpw.mods.fml.common.Loader;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Whether a Battle Tower site already holds a Recurrent Complex or Ruins structure. Both mods record what they build;
 * neither knows about Battle Towers, which built into them. Reached by reflection, so neither is needed to build.
 */
public final class TowerSites {

    private static Method rcGet, rcEntriesAt;
    private static Object ruinsMod;
    private static Method ruinsHandle;
    private static Field ruinsGenerator, ruinsRegistered, xMin, yMin, zMin, xMax, yMax, zMax;
    private static boolean rcBroken, ruinsBroken;

    private TowerSites() {}

    /** A tower stands on x-7..x+6 and z-7..z+6 around its site, from up to 70 below the surface to 56 above. */
    public static boolean occupied(World world, int x, int y, int z) {
        int minX = x - 8, maxX = x + 7, minY = Math.max(0, y - 72), maxY = y + 64, minZ = z - 8, maxZ = z + 7;
        return recurrentComplexAt(world, minX, minY, minZ, maxX, maxY, maxZ)
            || ruinsAt(world, minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static boolean recurrentComplexAt(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (rcBroken || !Loader.isModLoaded("reccomplex")) return false;
        try {
            if (rcGet == null) {
                Class<?> data = Class.forName("ivorius.reccomplex.worldgen.StructureGenerationData");
                rcEntriesAt = data.getMethod("getEntriesAt", StructureBoundingBox.class);
                rcGet = data.getMethod("get", World.class);
            }
            Object data = rcGet.invoke(null, world);
            return data != null && !((Collection<?>) rcEntriesAt
                .invoke(data, new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ))).isEmpty();
        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            rcBroken = true;
            AdventuraArcanaCore.LOG.warn("Battle Towers can't see Recurrent Complex's structures any more", e);
            return false;
        }
    }

    private static boolean ruinsAt(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (ruinsBroken || !Loader.isModLoaded("AS_Ruins")) return false;
        try {
            if (ruinsHandle == null) {
                ruinsMod = Loader.instance()
                    .getIndexedModList()
                    .get("AS_Ruins")
                    .getMod();
                ruinsGenerator = accessible(
                    Class.forName("atomicstryker.ruins.common.RuinsMod$WorldHandle")
                        .getDeclaredField("generator"));
                ruinsRegistered = accessible(
                    Class.forName("atomicstryker.ruins.common.RuinGenerator")
                        .getDeclaredField("registeredRuins"));
                Class<?> ruin = Class.forName("atomicstryker.ruins.common.RuinData");
                xMin = ruin.getField("xMin");
                yMin = ruin.getField("yMin");
                zMin = ruin.getField("zMin");
                xMax = ruin.getField("xMax");
                yMax = ruin.getField("yMax");
                zMax = ruin.getField("zMax");
                Method handle = ruinsMod.getClass()
                    .getDeclaredMethod("getWorldHandle", World.class);
                handle.setAccessible(true);
                ruinsHandle = handle;
            }
            Object handle = ruinsHandle.invoke(ruinsMod, world);
            Object generator = handle == null ? null : ruinsGenerator.get(handle);
            if (generator == null) return false;
            for (Object ruin : (Collection<?>) ruinsRegistered.get(generator)) {
                if (xMin.getInt(ruin) <= maxX && xMax.getInt(ruin) >= minX
                    && yMin.getInt(ruin) <= maxY
                    && yMax.getInt(ruin) >= minY
                    && zMin.getInt(ruin) <= maxZ
                    && zMax.getInt(ruin) >= minZ) return true;
            }
            return false;
        } catch (ReflectiveOperationException | LinkageError | ClassCastException | NullPointerException e) {
            ruinsBroken = true;
            AdventuraArcanaCore.LOG.warn("Battle Towers can't see Ruins' ruins any more", e);
            return false;
        }
    }

    private static Field accessible(Field field) {
        field.setAccessible(true);
        return field;
    }
}
