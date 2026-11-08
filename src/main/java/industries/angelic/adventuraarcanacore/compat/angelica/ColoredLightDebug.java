package industries.angelic.adventuraarcanacore.compat.angelica;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;

import com.gtnewhorizons.angelica.api.BlockLightProvider;
import com.gtnewhorizons.angelica.api.TintRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.compat.supernova.LightColorCommand;
import industries.angelic.adventuraarcanacore.compat.supernova.SupernovaColors;

/**
 * What /lightdebugblock and /lightdebughand share: tracing a block's colored light from the light engine to the vertex
 * colors. The command prints the light around the block on the client and the integrated server, then rebuilds its
 * chunk section; MixinChunkBuildTintDebug reports every face Angelica tints there: the per-corner block and sky light
 * it tints with, the tint, and the vertex colors before and after. Chat gets a line per face, the log every corner.
 */
public final class ColoredLightDebug {

    private static final long NONE = Long.MIN_VALUE;
    private static final int MAX_QUADS = 12;
    private static final int WAIT_TICKS = 100;
    private static final int RELIGHT_RADIUS = 2;

    /** The traced block, packed; read on Angelica's chunk build threads. */
    private static volatile long watched = NONE;
    private static final AtomicInteger quads = new AtomicInteger();
    private static final ConcurrentLinkedQueue<String> chat = new ConcurrentLinkedQueue<>();
    private static int ticksLeft;

    private ColoredLightDebug() {}

    public static void register() {
        ClientCommandHandler.instance.registerCommand(new BlockLightDebug());
        ClientCommandHandler.instance.registerCommand(new HandLightDebug());
        FMLCommonHandler.instance()
            .bus()
            .register(new Drain());
    }

    /** Whether the chunk builder should trace the quad it is tinting at this block, and counts it if so. */
    public static boolean trace(int x, int y, int z) {
        return watched == pack(x, y, z) && quads.incrementAndGet() <= MAX_QUADS;
    }

    /** A line for chat; also logged. Safe from any thread. */
    public static void say(String line) {
        AdventuraArcanaCore.LOG.info("[lightdebug] {}", line);
        chat.add(line);
    }

    /** A detail line for the log only. */
    public static void log(String line) {
        AdventuraArcanaCore.LOG.info("[lightdebug]   {}", line);
    }

    private static long pack(int x, int y, int z) {
        return ((long) x & 0x3FFFFFF) << 38 | ((long) z & 0x3FFFFFF) << 12 | (y & 0xFFF);
    }

    /** The tint setup, then the light at the block and in front of the face (or above, with no face). */
    static void describe(World world, int x, int y, int z, int side) {
        say(
            String.format(
                Locale.ROOT,
                "%s at %d %d %d, face %s; tint mode %s; provider %s, colored light %s; skylightSubtracted %d",
                Block.blockRegistry.getNameForObject(world.getBlock(x, y, z)),
                x,
                y,
                z,
                side < 0 ? "-" : String.valueOf(side),
                TintRegistry.getModeName(TintRegistry.getCurrentOrdinal()),
                BlockLightProvider.isRegistered() ? "registered" : "missing",
                BlockLightProvider.coloredLightEnabled() ? "on" : "off",
                world.skylightSubtracted));
        World server = serverWorld(world.provider.dimensionId);
        describe("block", world, server, x, y, z);
        int[] front = side < 0 ? new int[] { 0, 1, 0 }
            : new int[] { Facing.offsetsXForSide[side], Facing.offsetsYForSide[side], Facing.offsetsZForSide[side] };
        describe(side < 0 ? "above" : "front", world, server, x + front[0], y + front[1], z + front[2]);
    }

    /** Rebuilds the block's section and reports the faces Angelica tints there over the next ticks. */
    static void watch(int x, int y, int z) {
        quads.set(0);
        ticksLeft = WAIT_TICKS;
        watched = pack(x, y, z);
        Minecraft.getMinecraft().renderGlobal.markBlockForUpdate(x, y, z);
    }

    /** Relights the chunks around the player, so placed lights show a changed level: client now, server on its tick. */
    static void relightAround() {
        Minecraft mc = Minecraft.getMinecraft();
        int x = MathHelper.floor_double(mc.thePlayer.posX), z = MathHelper.floor_double(mc.thePlayer.posZ);
        SupernovaColors.relight(mc.theWorld, x, z, RELIGHT_RADIUS);
        World server = serverWorld(mc.theWorld.provider.dimensionId);
        if (server != null) LightColorCommand.onServer(() -> SupernovaColors.relight(server, x, z, RELIGHT_RADIUS));
    }

    /** "emit" with an optional level: the level (0 to 15), or null to toggle. */
    static Integer emitLevel(ICommandSender sender, String[] args, int at) {
        return args.length > at ? CommandBase.parseIntBounded(sender, args[at], 0, 15) : null;
    }

    private static World serverWorld(int dimension) {
        MinecraftServer server = MinecraftServer.getServer();
        return server == null ? null : server.worldServerForDimension(dimension);
    }

    private static void describe(String label, World client, World server, int x, int y, int z) {
        say(
            label + " "
                + x
                + " "
                + y
                + " "
                + z
                + ": client "
                + light(client, x, y, z)
                + (server == null ? "" : "; server " + light(server, x, y, z)));
    }

    /** Vanilla block and sky light, then the R/G/B block and sky light Angelica reads from the provider. */
    private static String light(World world, int x, int y, int z) {
        StringBuilder out = new StringBuilder().append("block ")
            .append(world.getSavedLightValue(EnumSkyBlock.Block, x, y, z))
            .append(" sky ")
            .append(world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
        if (BlockLightProvider.isRegistered()) {
            BlockLightProvider provider = BlockLightProvider.getInstance();
            out.append(" rgb ")
                .append(rgb(provider.getBlockLightRGB(world, x, y, z)))
                .append(" skyrgb ")
                .append(rgb(provider.getSkyLightRGB(world, x, y, z)));
        }
        return out.toString();
    }

    static String rgb(int packed) {
        return "(" + BlockLightProvider.unpackR(
            packed) + "," + BlockLightProvider.unpackG(packed) + "," + BlockLightProvider.unpackB(packed) + ")";
    }

    /** Client command basics both share. */
    abstract static class Command extends CommandBase {

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return true;
        }
    }

    public static final class Drain {

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getMinecraft();
            for (String line; (line = chat.poll()) != null;)
                if (mc.thePlayer != null) mc.thePlayer.addChatMessage(new ChatComponentText(line));
            if (watched == NONE || --ticksLeft > 0) return;
            if (quads.get() == 0) say(
                "No face of it went through Angelica's colored tint in " + WAIT_TICKS
                    + " ticks: not rebuilt, or drawn on another path");
            else say(Math.min(quads.get(), MAX_QUADS) + " face(s) traced; per-corner detail in the log");
            watched = NONE;
        }
    }
}
