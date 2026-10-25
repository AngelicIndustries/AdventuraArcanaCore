package industries.angelic.adventuraarcanacore.food;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Wild plants of other mods that yield HarvestCraft food (proposal v3, "link"): BOP's wild rice drops rice, sunflowers
 * drop sunflower seeds, PMP's prickly lettuce drops lettuce, and so on, each with a chance of the crop's seed so it
 * can be farmed. PMP's berry bushes give HarvestCraft's berries in place of their own (see MixinPmpBerryLinks).
 */
public final class FoodLinks {

    private static final int ANY = -1;
    private static volatile Map<String, String> berries = java.util.Collections.emptyMap();

    private final Map<Block, Map<Integer, String>> links;

    private FoodLinks(Map<Block, Map<Integer, String>> links) {
        this.links = links;
    }

    private static final String LINKS = "/assets/adventuraarcanacore/food_links.json";

    /** The links in {@code food_links.json}: plant id (mod:name[:meta]) to HarvestCraft crop. */
    public static void register() {
        Map<String, String> links = new java.util.LinkedHashMap<>();
        try (java.io.InputStream in = FoodLinks.class.getResourceAsStream(LINKS)) {
            if (in == null) throw new IllegalStateException("Missing " + LINKS);
            com.google.gson.JsonObject root = new com.google.gson.JsonParser()
                .parse(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8))
                .getAsJsonObject();
            for (Map.Entry<String, com.google.gson.JsonElement> link : root.entrySet()) links.put(
                link.getKey(),
                link.getValue()
                    .getAsString());
        } catch (java.io.IOException error) {
            throw new IllegalStateException("Unreadable " + LINKS, error);
        }
        register(links);
    }

    static void register(Map<String, String> links) {
        if (!PamFood.installed()) return;
        Map<Block, Map<Integer, String>> resolved = new HashMap<>();
        Map<String, String> bushes = new HashMap<>();
        for (Map.Entry<String, String> link : links.entrySet()) {
            String id = link.getKey();
            if (PamFood.produce(link.getValue()) == null) {
                AdventuraArcanaCore.LOG.warn("Food link {} names no HarvestCraft item {}", id, link.getValue());
                continue;
            }
            String[] parts = id.split(":");
            if (parts[0].equals("plantmegapack") && parts[1].startsWith("berrybush")) {
                bushes.put(parts[1], link.getValue());
                continue;
            }
            Block block = Block.getBlockFromName(parts[0] + ":" + parts[1]);
            if (block == null || block == Blocks.air) continue;
            int meta = parts.length > 2 ? Integer.parseInt(parts[2]) : ANY;
            resolved.computeIfAbsent(block, key -> new HashMap<>())
                .put(meta, link.getValue());
        }
        berries = java.util.Collections.unmodifiableMap(new HashMap<>(bushes));
        MinecraftForge.EVENT_BUS.register(new FoodLinks(resolved));
        AdventuraArcanaCore.LOG
            .info("HarvestCraft food links: {} plants, {} berry bushes", resolved.size(), bushes.size());
    }

    /** HarvestCraft's berry for a PMP berry bush (by PMP's name, berrybushBlack), or null. */
    public static Item berry(String bush) {
        String crop = berries.get(bush);
        return crop == null ? null : PamFood.produce(crop);
    }

    @SubscribeEvent
    public void harvest(BlockEvent.HarvestDropsEvent event) {
        if (event.world.isRemote || event.isSilkTouching) return;
        Map<Integer, String> byMeta = links.get(event.block);
        if (byMeta == null) return;
        int meta = event.blockMetadata;
        if (event.block == Blocks.double_plant && (meta & 8) != 0)
            meta = event.world.getBlock(event.x, event.y - 1, event.z) == Blocks.double_plant
                ? event.world.getBlockMetadata(event.x, event.y - 1, event.z)
                : meta;
        String crop = byMeta.containsKey(meta) ? byMeta.get(meta) : byMeta.get(ANY);
        if (crop == null) return;
        Item produce = PamFood.produce(crop), seed = PamFood.seed(crop);
        event.drops.add(new ItemStack(produce, 1 + event.world.rand.nextInt(2)));
        if (seed != null && event.world.rand.nextBoolean()) event.drops.add(new ItemStack(seed));
    }
}
