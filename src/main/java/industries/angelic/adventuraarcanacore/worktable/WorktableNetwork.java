package industries.angelic.adventuraarcanacore.worktable;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public final class WorktableNetwork {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("aac_worktable");
    private static final Queue<Runnable> PENDING = new ArrayBlockingQueue<>(256);

    private WorktableNetwork() {}

    public static void init() {
        CHANNEL.registerMessage(TransferHandler.class, Transfer.class, 0, Side.SERVER);
        FMLCommonHandler.instance()
            .bus()
            .register(new WorktableNetwork());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (int i = 0; i < 64; i++) {
            Runnable action = PENDING.poll();
            if (action == null) break;
            action.run();
        }
    }

    public static final class Transfer implements IMessage {

        private int window;
        private final ItemStack[] pattern = new ItemStack[9];
        private ItemStack output;

        public Transfer() {}

        public Transfer(int window, ItemStack[] pattern, ItemStack output) {
            this.window = window;
            for (int i = 0; i < 9; i++) this.pattern[i] = WorktableRecipes.copy(pattern[i]);
            this.output = output == null ? null : output.copy();
        }

        @Override
        public void toBytes(ByteBuf buffer) {
            buffer.writeInt(window);
            for (ItemStack stack : pattern) ByteBufUtils.writeItemStack(buffer, stack);
            ByteBufUtils.writeItemStack(buffer, output);
        }

        @Override
        public void fromBytes(ByteBuf buffer) {
            if (buffer.readableBytes() > 65536) throw new IllegalArgumentException("Worktable recipe packet too large");
            window = buffer.readInt();
            for (int i = 0; i < 9; i++) pattern[i] = WorktableRecipes.copy(ByteBufUtils.readItemStack(buffer));
            output = ByteBufUtils.readItemStack(buffer);
            if (buffer.isReadable()) throw new IllegalArgumentException("Unexpected worktable recipe data");
        }
    }

    public static final class TransferHandler implements IMessageHandler<Transfer, IMessage> {

        @Override
        public IMessage onMessage(Transfer message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            // Custom packets arrive on Netty's thread in Forge 1.7.10.
            PENDING.offer(() -> {
                if (player.openContainer instanceof ContainerWorktable container && container.windowId == message.window
                    && container.canInteractWith(player)) {
                    container.tile.setPattern(message.pattern);
                    container.tile.preferOutput(message.output);
                    container.detectAndSendChanges();
                }
            });
            return null;
        }
    }
}
