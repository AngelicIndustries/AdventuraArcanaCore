package industries.angelic.adventuraarcanacore.compat.realistictorches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import industries.angelic.adventuraarcanacore.Config;

/**
 * The burning torches of each loaded chunk, with the world time each next smolders or goes out. They are saved with
 * their chunk (AACTorches: the save's world time, then each torch's position in the chunk and the ticks it had left)
 * and dropped from memory when it unloads, so nothing is looked at for chunks nobody is near; with burnUnloaded, a
 * reloaded chunk's torches catch up on the time it was away. Once a second, the loaded chunks whose next deadline has
 * passed are gone through, and while it rains, those with torches that rain puts out; any other chunk costs a
 * comparison. A chunk saved without the tag, from before, is searched for burning torches once when it loads. A torch
 * enters when placed or lit, through its onBlockAdded, and leaves when it goes out or is no longer there.
 */
public final class TorchClock {

    private static final String TAG = "AACTorches";
    private static final int PERIOD = 20;
    private static final long NEVER = Long.MAX_VALUE;
    private static final Map<World, Map<Long, ChunkTorches>> WORLDS = new WeakHashMap<>();

    private static final class Torch {

        long deadline;
        /** Since when the rain has reached it, or -1. */
        long wet = -1;
    }

    private static final class ChunkTorches {

        final Map<Integer, Torch> byPosition = new HashMap<>();
        boolean loaded = true;
        long next = NEVER;
    }

    private TorchClock() {}

    public static void register() {
        TorchClock clock = new TorchClock();
        MinecraftForge.EVENT_BUS.register(clock);
        FMLCommonHandler.instance()
            .bus()
            .register(clock);
    }

    /** From a lit or smoldering torch's onBlockAdded: its time starts. */
    public static void placed(World world, int x, int y, int z, Block block) {
        if (world.isRemote) return;
        Torches.Kind lit = Torches.litKind(block);
        if (lit != null) set(world, x, y, z, lit, lit.litTicks());
        else {
            Torches.Kind smoldering = Torches.smolderingKind(block);
            if (smoldering != null) set(world, x, y, z, smoldering, smoldering.smolderTicks());
        }
    }

    /** After a lighter lit the torch, which may have been lit already, and so not added again. */
    public static void lit(World world, int x, int y, int z, Torches.Kind kind) {
        if (!world.isRemote) set(world, x, y, z, kind, kind.litTicks());
    }

    /** The sound of a torch going out. */
    static void fizz(World world, int x, int y, int z) {
        world.playSoundEffect(
            x + 0.5,
            y + 0.5,
            z + 0.5,
            "random.fizz",
            0.5F,
            2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
    }

    private static void set(World world, int x, int y, int z, Torches.Kind kind, long ticks) {
        Map<Long, ChunkTorches> chunks = WORLDS.computeIfAbsent(world, w -> new HashMap<>());
        long key = ChunkCoordIntPair.chunkXZ2Int(x >> 4, z >> 4);
        if (!kind.goesOut()) {
            ChunkTorches torches = chunks.get(key);
            if (torches != null) torches.byPosition.remove(position(x, y, z));
            return;
        }
        ChunkTorches torches = chunks.computeIfAbsent(key, k -> new ChunkTorches());
        Torch torch = torches.byPosition.computeIfAbsent(position(x, y, z), p -> new Torch());
        torch.deadline = kind.burnout() > 0 ? world.getTotalWorldTime() + ticks : NEVER;
        torch.wet = -1;
        torches.next = Math.min(torches.next, torch.deadline);
    }

    private static int position(int x, int y, int z) {
        return x & 15 | (z & 15) << 4 | y << 8;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (event.phase != TickEvent.Phase.END || world.isRemote || world.getTotalWorldTime() % PERIOD != 0) return;
        Map<Long, ChunkTorches> chunks = WORLDS.get(world);
        if (chunks == null || chunks.isEmpty()) return;
        long now = world.getTotalWorldTime();
        boolean raining = world.isRaining();
        for (Map.Entry<Long, ChunkTorches> chunk : new ArrayList<>(chunks.entrySet())) {
            ChunkTorches torches = chunk.getValue();
            if (!torches.loaded || torches.byPosition.isEmpty() || torches.next > now && !raining) continue;
            update(world, chunk.getKey(), torches, now, raining);
        }
    }

    private static void update(World world, long key, ChunkTorches torches, long now, boolean raining) {
        int chunkX = (int) key, chunkZ = (int) (key >> 32);
        long next = NEVER;
        for (Integer position : torches.byPosition.keySet()
            .toArray(new Integer[0])) {
            Torch torch = torches.byPosition.get(position);
            int x = chunkX << 4 | position & 15, z = chunkZ << 4 | position >> 4 & 15, y = position >>> 8;
            Block block = world.getBlock(x, y, z);
            Torches.Kind kind = Torches.litKind(block);
            boolean lit = kind != null;
            if (!lit) kind = Torches.smolderingKind(block);
            if (torch == null || kind == null) {
                torches.byPosition.remove(position);
                continue;
            }
            if (raining && kind.rain() && world.canLightningStrikeAt(x, y, z)) {
                if (torch.wet < 0) torch.wet = now;
                if (now - torch.wet >= Config.torchRainDelay) {
                    goOut(world, x, y, z, kind);
                    torches.byPosition.remove(position);
                    continue;
                }
            } else torch.wet = -1;
            // Catching up after a long time away can take it through smoldering and out at once.
            while (torch.deadline <= now) {
                if (lit && kind.smoldering() != null) {
                    long deadline = torch.deadline + kind.smolderTicks();
                    world.setBlock(x, y, z, kind.smoldering(), world.getBlockMetadata(x, y, z), 3);
                    torch.deadline = deadline;
                    lit = false;
                } else {
                    goOut(world, x, y, z, kind);
                    torches.byPosition.remove(position);
                    torch = null;
                    break;
                }
            }
            if (torch != null) next = Math.min(next, torch.deadline);
        }
        torches.next = next;
    }

    private static void goOut(World world, int x, int y, int z, Torches.Kind kind) {
        fizz(world, x, y, z);
        Block unlit = kind.unlit();
        if (unlit != null) world.setBlock(x, y, z, unlit, world.getBlockMetadata(x, y, z), 3);
        else world.setBlockToAir(x, y, z);
    }

    @SubscribeEvent
    public void onChunkDataLoad(ChunkDataEvent.Load event) {
        Chunk chunk = event.getChunk();
        World world = chunk.worldObj;
        if (world.isRemote) return;
        ChunkTorches torches = new ChunkTorches();
        long now = world.getTotalWorldTime();
        NBTTagCompound data = event.getData();
        if (data.hasKey(TAG)) {
            NBTTagCompound tag = data.getCompoundTag(TAG);
            long from = Config.torchesBurnUnloaded ? tag.getLong("saved") : now;
            int[] positions = tag.getIntArray("p"), left = tag.getIntArray("r");
            for (int i = 0; i < Math.min(positions.length, left.length); i++) {
                Torch torch = new Torch();
                torch.deadline = left[i] == Integer.MAX_VALUE ? NEVER : from + left[i];
                torches.byPosition.put(positions[i], torch);
                torches.next = Math.min(torches.next, torch.deadline);
            }
        } else search(chunk, torches, now);
        WORLDS.computeIfAbsent(world, w -> new HashMap<>())
            .put(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition), torches);
    }

    /** The burning torches of a chunk saved before AAC kept them, each from the start of its time. */
    private static void search(Chunk chunk, ChunkTorches torches, long now) {
        for (ExtendedBlockStorage storage : chunk.getBlockStorageArray()) {
            if (storage == null || storage.isEmpty()) continue;
            for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) for (int x = 0; x < 16; x++) {
                Block block = storage.getBlockByExtId(x, y, z);
                Torches.Kind kind = Torches.litKind(block);
                long ticks = kind == null ? 0 : kind.litTicks();
                if (kind == null) {
                    kind = Torches.smolderingKind(block);
                    if (kind != null) ticks = kind.smolderTicks();
                }
                if (kind == null || !kind.goesOut()) continue;
                Torch torch = new Torch();
                torch.deadline = kind.burnout() > 0 ? now + ticks : NEVER;
                torches.byPosition.put(position(x, storage.getYLocation() + y, z), torch);
                torches.next = Math.min(torches.next, torch.deadline);
            }
        }
    }

    /** New chunks have no saved torches: theirs enter through onBlockAdded as the chunk is populated. */
    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.world.isRemote) return;
        Chunk chunk = event.getChunk();
        WORLDS.computeIfAbsent(event.world, w -> new HashMap<>())
            .computeIfAbsent(
                ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition),
                k -> new ChunkTorches()).loaded = true;
    }

    /** The unloaded chunk's torches stay until its save, which follows. */
    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.world.isRemote) return;
        Map<Long, ChunkTorches> chunks = WORLDS.get(event.world);
        Chunk chunk = event.getChunk();
        ChunkTorches torches = chunks == null ? null
            : chunks.get(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition));
        if (torches != null) torches.loaded = false;
    }

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event) {
        Chunk chunk = event.getChunk();
        World world = chunk.worldObj;
        if (world.isRemote) return;
        Map<Long, ChunkTorches> chunks = WORLDS.get(world);
        long key = ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition);
        ChunkTorches torches = chunks == null ? null : chunks.get(key);
        long now = world.getTotalWorldTime();
        int count = torches == null ? 0 : torches.byPosition.size(), i = 0;
        int[] positions = new int[count], left = new int[count];
        if (torches != null) for (Map.Entry<Integer, Torch> torch : torches.byPosition.entrySet()) {
            long deadline = torch.getValue().deadline;
            positions[i] = torch.getKey();
            left[i++] = deadline == NEVER ? Integer.MAX_VALUE
                : (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE - 1, deadline - now));
        }
        // Saved even when empty: the chunk needn't be searched again.
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("saved", now);
        tag.setIntArray("p", positions);
        tag.setIntArray("r", left);
        event.getData()
            .setTag(TAG, tag);
        if (torches != null && !chunk.isChunkLoaded) chunks.remove(key);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        WORLDS.remove(event.world);
    }

    /** A Netherlicious lit torch that goes out drops its unlit torch, as Realistic Torches' lit torch does. */
    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        Item unlit = Torches.unlitDrop(event.block);
        if (unlit == null || Torches.litKind(event.block) == Torches.Kind.TORCH) return;
        int count = 0;
        for (ItemStack stack : event.drops) count += stack.stackSize;
        event.drops.clear();
        if (count > 0) event.drops.add(new ItemStack(unlit, count));
    }
}
