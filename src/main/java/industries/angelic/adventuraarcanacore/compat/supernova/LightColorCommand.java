package industries.angelic.adventuraarcanacore.compat.supernova;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.ClientCommandHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * /lightcolor [hand]: opens GuiLightColor for the block under the crosshair, or for the held item when looking at no
 * block or given "hand". Also runs work GuiLightColor queues for the integrated server's thread.
 */
public final class LightColorCommand extends CommandBase {

    /** Shown on the next tick, once the chat that ran the command has closed. */
    private static GuiLightColor pending;
    private static final ConcurrentLinkedQueue<Runnable> serverTasks = new ConcurrentLinkedQueue<>();

    private LightColorCommand() {}

    public static void register() {
        ClientCommandHandler.instance.registerCommand(new LightColorCommand());
        FMLCommonHandler.instance()
            .bus()
            .register(new Ticks());
    }

    /** Runs on the integrated server's thread at its next tick. */
    public static void onServer(Runnable task) {
        serverTasks.add(task);
    }

    @Override
    public String getCommandName() {
        return "lightcolor";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lightcolor [hand]";
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
        boolean hand = args.length > 0 && args[0].equals("hand");
        MovingObjectPosition hit = mc.objectMouseOver;
        if (!hand && hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block block = mc.theWorld.getBlock(hit.blockX, hit.blockY, hit.blockZ);
            pending = GuiLightColor.forBlock(block, mc.theWorld.getBlockMetadata(hit.blockX, hit.blockY, hit.blockZ));
            return;
        }
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null) {
            sender.addChatMessage(new ChatComponentTranslation("adventuraarcanacore.lightcolor.nothing"));
            return;
        }
        pending = GuiLightColor.forItem(held);
    }

    public static final class Ticks {

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || pending == null) return;
            Minecraft.getMinecraft()
                .displayGuiScreen(pending);
            pending = null;
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            for (Runnable task; (task = serverTasks.poll()) != null;) task.run();
        }
    }
}
