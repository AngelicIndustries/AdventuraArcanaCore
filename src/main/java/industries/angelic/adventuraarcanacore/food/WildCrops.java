package industries.angelic.adventuraarcanacore.food;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * Wild crops: HarvestCraft's crops grow wild on natural ground and Harvest the Nether's on nether ground, where their
 * own rules allow only farmland and soul sand. Sowing still needs farmland; a harvested wild plant regrows in place.
 */
public final class WildCrops {

    private static final class Rules {

        final Class<?> crop, netherCrop;
        final Set<Block> netherSoil;

        Rules(Class<?> crop, Class<?> netherCrop, Set<Block> netherSoil) {
            this.crop = crop;
            this.netherCrop = netherCrop;
            this.netherSoil = netherSoil;
        }
    }

    private static volatile Rules rules;

    private WildCrops() {}

    /** From {@code BlockBush#canBlockStay}: whether this crop stays on the ground under it although its mod says no. */
    public static boolean stays(Block block, World world, int x, int y, int z) {
        if (!(block instanceof BlockCrops)) return false;
        Rules known = rules();
        Class<?> type = block.getClass();
        if (type != known.crop && type != known.netherCrop) return false;
        Block soil = world.getBlock(x, y - 1, z);
        return type == known.crop ? naturalGround(soil) : known.netherSoil.contains(soil);
    }

    /** Grass, dirt of any kind (podzol, coarse) and sand for the coastal crops. */
    public static boolean naturalGround(Block soil) {
        Material material = soil.getMaterial();
        return material == Material.grass || material == Material.ground && soil != Blocks.farmland
            || soil == Blocks.sand;
    }

    public static boolean netherGround(Block soil) {
        return rules().netherSoil.contains(soil);
    }

    private static Rules rules() {
        Rules known = rules;
        if (known == null) {
            Set<Block> nether = new HashSet<>(java.util.Arrays.asList(Blocks.netherrack, Blocks.soul_sand));
            for (String id : new String[] { "etfuturum:nylium", "etfuturum:soul_soil" }) {
                Block block = Block.getBlockFromName(id);
                if (block != null && block != Blocks.air) nether.add(block);
            }
            known = rules = new Rules(
                type("com.pam.harvestcraft.BlockPamCrop"),
                type("com.pam.harvestthenether.BlockPamCrop"),
                java.util.Collections.unmodifiableSet(nether));
        }
        return known;
    }

    private static Class<?> type(String name) {
        try {
            return Class.forName(name, false, WildCrops.class.getClassLoader());
        } catch (ClassNotFoundException | LinkageError absent) {
            return null;
        }
    }
}
