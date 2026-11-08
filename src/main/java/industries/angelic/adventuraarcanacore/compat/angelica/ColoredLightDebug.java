package industries.angelic.adventuraarcanacore.compat.angelica;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Facing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;

import com.gtnewhorizons.angelica.api.BlockLightProvider;
import com.gtnewhorizons.angelica.api.TintRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * /lightdebug: traces the colored light of the block under the crosshair (or at x y z) from the light engine to the
 * vertex colors. Prints the light at the block and in front of the face looked at, on the client and the integrated
 * server, then rebuilds the block's chunk section; MixinChunkBuildTintDebug reports every face Angelica tints there:
 * the per-corner block and sky light it tints with, the tint, and the vertex colors before and after. Chat gets a line
 * per face, the log every corner.
 */
public final class ColoredLightDebug extends CommandBase {

    private static final long NONE = Long.MIN_VALUE;
    private static final int MAX_QUADS = 12;
    private static final int WAIT_TICKS = 60;

    /** The traced block, packed; read on Angelica's chunk build threads. */
    private static volatile long watched = NONE;
    private static final AtomicInteger quads = new AtomicInteger();
    private static final ConcurrentLinkedQueue<String> chat = new ConcurrentLinkedQueue<>();
    private static int ticksLeft;

    private ColoredLightDebug() {}

    public static void register() {
        ClientCommandHandler.instance.registerCommand(new ColoredLightDebug());
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

    @Override
    public String getCommandName() {
        return "lightdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lightdebug [x y z]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        int x, y, z, side = -1;
        if (args.length == 3) {
            x = parseInt(sender, args[0]);
            y = parseInt(sender, args[1]);
            z = parseInt(sender, args[2]);
        } else if (mc.objectMouseOver != null
            && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                x = mc.objectMouseOver.blockX;
                y = mc.objectMouseOver.blockY;
                z = mc.objectMouseOver.blockZ;
                side = mc.objectMouseOver.sideHit;
            } else {
                sender.addChatMessage(new ChatComponentText("Look at a block, or give x y z."));
                return;
            }
        Block block = world.getBlock(x, y, z);
        say(
            String.format(
                Locale.ROOT,
                "%s at %d %d %d, face %s; tint mode %s; provider %s, colored light %s; skylightSubtracted %d",
                Block.blockRegistry.getNameForObject(block),
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
        if (side >= 0) describe(
            "front",
            world,
            server,
            x + Facing.offsetsXForSide[side],
            y + Facing.offsetsYForSide[side],
            z + Facing.offsetsZForSide[side]);
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held != null && held.getItem() instanceof ItemBlock) {
            int light = Block.getBlockFromItem(held.getItem())
                .getLightValue();
            if (light > 0) say(
                "Holding " + held.getDisplayName()
                    + " (light "
                    + light
                    + "): its dynamic light adds to the faces traced");
        }
        quads.set(0);
        ticksLeft = WAIT_TICKS;
        watched = pack(x, y, z);
        mc.renderGlobal.markBlockForUpdate(x, y, z);
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

    private static String rgb(int packed) {
        return "(" + BlockLightProvider.unpackR(
            packed) + "," + BlockLightProvider.unpackG(packed) + "," + BlockLightProvider.unpackB(packed) + ")";
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
