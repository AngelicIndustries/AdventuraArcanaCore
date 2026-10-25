package industries.angelic.adventuraarcanacore.food;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * A shoot for each bamboo species, in place of HarvestCraft's one generic shoot crop, and the shoot item they drop and
 * are planted from. Registered only with HarvestCraft installed: the shoot grows and cooks like its crops, and is drawn
 * from its texture. They came from Biomic, which remaps its old names to them in older worlds.
 */
public final class BambooShoots {

    private static final Map<BambooSpecies, BlockBambooShoot> SHOOTS = new EnumMap<>(BambooSpecies.class);
    private static ItemBambooShoot item;
    private static boolean registered;

    private BambooShoots() {}

    public static synchronized void register() {
        if (registered || !PamFood.installed()) return;
        registered = true;
        item = new ItemBambooShoot();
        GameRegistry.registerItem(item, "bamboo_shoot");
        for (String ore : new String[] { "cropBambooshoot", "listAllveggie", "foodBambooshoot" })
            OreDictionary.registerOre(ore, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
        for (BambooSpecies species : BambooSpecies.values()) {
            BlockBambooShoot shoot = new BlockBambooShoot(species);
            GameRegistry.registerBlock(shoot, (Class<? extends ItemBlock>) null, "bamboo_shoot_" + species.id);
            SHOOTS.put(species, shoot);
        }
        AdventuraArcanaCore.LOG.info("Bamboo shoots: {}", SHOOTS.keySet());
    }

    /** The shoot plant of a bamboo species, or null without HarvestCraft. */
    public static BlockBambooShoot shoot(BambooSpecies species) {
        return SHOOTS.get(species);
    }

    /** The shoot item (metadata: BambooSpecies order), or null without HarvestCraft. */
    public static ItemBambooShoot item() {
        return item;
    }
}
