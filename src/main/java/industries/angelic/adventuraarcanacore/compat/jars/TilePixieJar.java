package industries.angelic.adventuraarcanacore.compat.jars;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;

/**
 * The pixie inside a pixie jar. All of it is the client's: it flies about, leaves sparkles where it goes and makes its
 * sound now and then, as a free pixie does, only quieter through the glass.
 */
public class TilePixieJar extends TileEntity {

    private final Random random = new Random();

    public int age;
    /** As a mob's: it sounds once a draw falls under this, then waits again. */
    private int soundTime = -random.nextInt(80);

    long seed() {
        return (long) xCoord * 31 + yCoord * 17L + zCoord;
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || !worldObj.isRemote) return;
        age++;
        // Fewer sparkles than a free pixie leaves, as it has less room
        if (random.nextInt(2) == 0) {
            float[] at = PixieFlight.at(age, seed());
            PixieJars.trail(
                xCoord + at[0] + (random.nextDouble() - 0.5) * 0.15,
                yCoord + at[1] + PixieFlight.bodyHeight() + (random.nextDouble() - 0.5) * 0.15,
                zCoord + at[2] + (random.nextDouble() - 0.5) * 0.15);
        }
        if (random.nextInt(1000) < soundTime++) {
            soundTime = -80;
            worldObj.playSound(
                xCoord + 0.5,
                yCoord + 0.5,
                zCoord + 0.5,
                PixieJars.PIXIE_SOUND,
                0.3F,
                (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F,
                false);
        }
    }
}
