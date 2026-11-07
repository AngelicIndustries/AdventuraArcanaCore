package industries.angelic.adventuraarcanacore.compat.realistictorches;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.Config;

/**
 * Lighting a torch that burns out, lit, smoldering or unlit, with a lighter: the vanilla flint and steel and the
 * torchLighters give it a normal flame, Netherlicious' soul, foxfire and shadow flint and steels theirs, the Aether's
 * Zanite and Cinerarium hellfire, its Zanite and Cyro cold fire and AAC's Zanite and Ambrosium ambrosium flame. The
 * torch keeps its material (wood or bone) and facing. Each use wears the lighter; rain that would put the torch out
 * keeps it from being lit.
 */
public final class TorchLighting {

    private static volatile Map<Item, Torches.Flame> lighters;

    private TorchLighting() {}

    /** Lights the torch if the player holds a lighter, as its onBlockActivated; whether it was used. */
    public static boolean light(World world, int x, int y, int z, EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null) return false;
        Torches.Flame flame = lighters().get(stack.getItem());
        Torches.Kind kind = flame == null ? null : Torches.toLight(world.getBlock(x, y, z), flame);
        if (kind == null) return false;
        stack.damageItem(1, player);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
        if (kind.rain() && world.canLightningStrikeAt(x, y, z)) {
            TorchClock.fizz(world, x, y, z);
            return true;
        }
        world.setBlock(x, y, z, kind.lit(), world.getBlockMetadata(x, y, z), 3);
        TorchClock.lit(world, x, y, z, kind);
        return true;
    }

    private static Map<Item, Torches.Flame> lighters() {
        if (lighters == null) {
            Map<Item, Torches.Flame> map = new HashMap<>();
            map.put(Items.flint_and_steel, Torches.Flame.FIRE);
            for (String name : Config.torchLighters) {
                Item item = (Item) Item.itemRegistry.getObject(name);
                if (item != null) map.put(item, Torches.Flame.FIRE);
            }
            put(map, "netherlicious", "FlintAndSteelSoul", Torches.Flame.SOUL);
            put(map, "netherlicious", "FlintAndSteelFoxfire", Torches.Flame.FOXFIRE);
            put(map, "netherlicious", "FlintAndSteelShadow", Torches.Flame.SHADOW);
            put(map, "aether_legacy", "zanite_and_cinerarium", Torches.Flame.HELLFIRE);
            put(map, "aether_legacy", "zanite_and_cyro", Torches.Flame.COLD);
            put(map, AdventuraArcanaCore.MODID, "zanite_and_ambrosium", Torches.Flame.AMBROSIUM);
            lighters = map;
        }
        return lighters;
    }

    private static void put(Map<Item, Torches.Flame> map, String mod, String name, Torches.Flame flame) {
        Item item = GameRegistry.findItem(mod, name);
        if (item != null) map.put(item, flame);
    }
}
