package industries.angelic.adventuraarcanacore.compat.netherlicious;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Netherlicious backports much of the Nether Update that Et Futurum already has: basalt, blackstone, crimson and warped
 * wood, chains, lanterns and more. Et Futurum's are the pack's. A Netherlicious item with an Et Futurum namesake (the
 * same English name) leaves the creative tabs and NEI, loses its recipes and drops as its namesake, and Netherlicious'
 * other recipes take the namesake instead. Its biomes and ruined portals still place its own blocks.
 */
public final class NetherliciousDuplicates {

    /** Netherlicious names that differ from Et Futurum's for the same thing. */
    private static final Map<String, String> ALIASES = Collections.singletonMap("Crimison Sign", "Crimson Sign");
    /** Every music disc is called "Music Disc", so discs pair by registry name. */
    private static final Map<String, String> DISCS = Collections
        .singletonMap("netherlicious:Pigstep", "etfuturum:pigstep_record");

    private static final Map<Long, ItemStack> NAMESAKES = new HashMap<>();
    private static final List<ItemStack> DUPLICATES = new ArrayList<>();

    private NetherliciousDuplicates() {}

    public static void apply() {
        Map<String, String> english = new HashMap<>();
        load(english, "/assets/minecraft/lang/en_US.lang");
        load(english, "/assets/etfuturum/lang/en_US.lang");
        load(english, "/assets/netherlicious/lang/en_US.lang");
        Map<String, ItemStack> futurum = new HashMap<>();
        Map<String, String> ambiguous = new TreeMap<>();
        for (ItemStack stack : stacks("etfuturum")) {
            String name = name(english, stack);
            // A double slab is named like its slab, and no player holds one.
            if (name == null || id(stack).startsWith("etfuturum:double_")) continue;
            ItemStack first = futurum.putIfAbsent(name, stack);
            if (first != null) ambiguous.put(name, id(first) + ", " + id(stack));
        }
        Map<String, String> paired = new TreeMap<>();
        java.util.Set<String> used = new java.util.HashSet<>();
        for (ItemStack stack : stacks("netherlicious")) {
            String name = name(english, stack);
            String disc = DISCS.get(Item.itemRegistry.getNameForObject(stack.getItem()));
            ItemStack namesake = disc != null ? find(disc)
                : name == null || stack.getItem() instanceof net.minecraft.item.ItemRecord ? null
                    : futurum.get(ALIASES.getOrDefault(name, name));
            if (namesake == null) continue;
            NAMESAKES.put(key(stack.getItem(), stack.getItemDamage()), namesake);
            DUPLICATES.add(stack);
            paired.put(id(stack), id(namesake) + " (" + name + ")");
            if (disc == null) used.add(ALIASES.getOrDefault(name, name));
        }
        ambiguous.keySet()
            .retainAll(used);
        hideFromTabs();
        int removed = fixRecipes();
        MinecraftForge.EVENT_BUS.register(new Swaps());
        FMLCommonHandler.instance()
            .bus()
            .register(new Swaps());
        AdventuraArcanaCore.LOG.info(
            "Netherlicious duplicates of Et Futurum: {} items, {} recipes removed: {}",
            NAMESAKES.size(),
            removed,
            paired);
        if (!ambiguous.isEmpty())
            AdventuraArcanaCore.LOG.info("Et Futurum names on several items, the first one taken: {}", ambiguous);
    }

    /** The duplicates, for NEI to hide. */
    public static Collection<ItemStack> duplicates() {
        return DUPLICATES;
    }

    /** Et Futurum's stack for a Netherlicious duplicate, or null. */
    public static ItemStack namesake(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;
        ItemStack namesake = NAMESAKES.get(key(stack.getItem(), stack.getItemDamage()));
        if (namesake == null) return null;
        ItemStack copy = namesake.copy();
        copy.stackSize = stack.stackSize;
        return copy;
    }

    private static String id(ItemStack stack) {
        return Item.itemRegistry.getNameForObject(stack.getItem()) + ":" + stack.getItemDamage();
    }

    private static long key(Item item, int meta) {
        return (long) Item.getIdFromItem(item) << 32 | meta & 0xFFFFFFFFL;
    }

    private static void load(Map<String, String> english, String path) {
        try (InputStream in = NetherliciousDuplicates.class.getResourceAsStream(path)) {
            if (in != null) english.putAll(StringTranslate.parseLangFile(in));
        } catch (IOException e) {
            AdventuraArcanaCore.LOG.warn("Couldn't read {}", path, e);
        }
    }

    /** Each of a mod's items and the metas whose names differ, without asking the client-only subtype lists. */
    private static List<ItemStack> stacks(String modid) {
        List<ItemStack> out = new ArrayList<>();
        for (Object key : Item.itemRegistry.getKeys()) {
            if (!((String) key).startsWith(modid + ":")) continue;
            Item item = (Item) Item.itemRegistry.getObject(key);
            if (item == null) continue;
            List<String> seen = new ArrayList<>();
            for (int meta = 0; meta < (item.getHasSubtypes() ? 16 : 1); meta++) {
                ItemStack stack = new ItemStack(item, 1, meta);
                String unlocalized;
                try {
                    unlocalized = item.getUnlocalizedName(stack);
                } catch (RuntimeException invalidMeta) {
                    continue;
                }
                if (unlocalized == null || seen.contains(unlocalized)) continue;
                seen.add(unlocalized);
                out.add(stack);
            }
        }
        return out;
    }

    private static String name(Map<String, String> english, ItemStack stack) {
        try {
            return english.get(
                stack.getItem()
                    .getUnlocalizedName(stack) + ".name");
        } catch (RuntimeException invalidMeta) {
            return null;
        }
    }

    private static ItemStack find(String name) {
        Item item = (Item) Item.itemRegistry.getObject(name);
        return item == null ? null : new ItemStack(item);
    }

    /** An item leaves its tab once all its metas are duplicates. */
    private static void hideFromTabs() {
        Map<Item, Integer> duplicated = new HashMap<>();
        for (ItemStack stack : DUPLICATES) duplicated.merge(stack.getItem(), 1, Integer::sum);
        Map<Item, Integer> all = new HashMap<>();
        for (ItemStack stack : stacks("netherlicious")) all.merge(stack.getItem(), 1, Integer::sum);
        for (Map.Entry<Item, Integer> entry : duplicated.entrySet()) {
            if (!entry.getValue()
                .equals(all.get(entry.getKey()))) continue;
            Item item = entry.getKey();
            item.setCreativeTab(null);
            if (item instanceof ItemBlock) Block.getBlockFromItem(item)
                .setCreativeTab(null);
        }
    }

    /** Drops duplicates' recipes and smelting, and points other recipes' duplicate ingredients at the namesakes. */
    @SuppressWarnings("unchecked")
    private static int fixRecipes() {
        int removed = 0;
        for (Iterator<IRecipe> it = CraftingManager.getInstance()
            .getRecipeList()
            .iterator(); it.hasNext();) {
            IRecipe recipe = it.next();
            if (namesake(recipe.getRecipeOutput()) != null) {
                it.remove();
                removed++;
            } else if (recipe instanceof ShapedRecipes shaped) {
                ItemStack[] items = shaped.recipeItems;
                for (int i = 0; i < items.length; i++) items[i] = swap(items[i]);
            } else if (recipe instanceof ShapelessRecipes shapeless) {
                List<ItemStack> items = shapeless.recipeItems;
                items.replaceAll(NetherliciousDuplicates::swap);
            } else if (recipe instanceof ShapedOreRecipe ore) {
                Object[] input = ore.getInput();
                for (int i = 0; i < input.length; i++) if (input[i] instanceof ItemStack stack) input[i] = swap(stack);
            } else if (recipe instanceof ShapelessOreRecipe ore) {
                ore.getInput()
                    .replaceAll(o -> o instanceof ItemStack stack ? swap(stack) : o);
            }
        }
        Map<ItemStack, ItemStack> smelting = FurnaceRecipes.smelting()
            .getSmeltingList();
        smelting.entrySet()
            .removeIf(e -> namesake(e.getKey()) != null || namesake(e.getValue()) != null);
        return removed;
    }

    private static ItemStack swap(ItemStack stack) {
        if (stack == null || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) return stack;
        ItemStack namesake = namesake(stack);
        return namesake != null ? namesake : stack;
    }

    /**
     * Duplicates broken out of Netherlicious' biomes and portals drop as their namesakes, and any that reach a player
     * another way (loot, mobs) turn into them in the inventory.
     */
    public static final class Swaps {

        @SubscribeEvent
        public void onHarvest(BlockEvent.HarvestDropsEvent event) {
            event.drops.replaceAll(NetherliciousDuplicates::swap);
        }

        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.player.worldObj.isRemote
                || event.player.ticksExisted % 20 != 0) return;
            ItemStack[] slots = event.player.inventory.mainInventory;
            for (int i = 0; i < slots.length; i++) {
                ItemStack namesake = namesake(slots[i]);
                if (namesake != null) slots[i] = namesake;
            }
        }
    }
}
