package industries.angelic.adventuraarcanacore.compartment;

import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public final class CompartmentNetwork {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("aac_compartments");
    private static final Queue<Runnable> PENDING = new ArrayBlockingQueue<>(1024);

    private CompartmentNetwork() {}

    public static void init() {
        CHANNEL.registerMessage(RenameHandler.class, Rename.class, 0, Side.SERVER);
        FMLCommonHandler.instance()
            .bus()
            .register(new CompartmentNetwork());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (int i = 0; i < 256; i++) {
            Runnable action = PENDING.poll();
            if (action == null) break;
            action.run();
        }
    }

    public static class Rename implements IMessage {

        private int window;
        private int tab;
        private String name = "";

        public Rename() {}

        public Rename(int window, int tab, String name) {
            this.window = window;
            this.tab = tab;
            this.name = name.substring(0, Math.min(name.length(), CompartmentTab.MAX_NAME_LENGTH));
        }

        @Override
        public void toBytes(ByteBuf buffer) {
            byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
            buffer.writeInt(window);
            buffer.writeByte(tab);
            buffer.writeByte(bytes.length);
            buffer.writeBytes(bytes);
        }

        @Override
        public void fromBytes(ByteBuf buffer) {
            window = buffer.readInt();
            tab = buffer.readUnsignedByte();
            int length = buffer.readUnsignedByte();
            if (length > CompartmentTab.MAX_NAME_LENGTH * 4 || length > buffer.readableBytes()) {
                throw new IllegalArgumentException("Invalid compartment tab name length");
            }
            byte[] bytes = new byte[length];
            buffer.readBytes(bytes);
            name = new String(bytes, StandardCharsets.UTF_8);
        }
    }

    public static class RenameHandler implements IMessageHandler<Rename, IMessage> {

        @Override
        public IMessage onMessage(Rename message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            // Forge 1.7.10 dispatches custom packets on Netty's thread.
            PENDING.offer(() -> {
                if (player.openContainer instanceof ContainerCompartment container
                    && container.windowId == message.window
                    && container.canInteractWith(player)
                    && container.tile.isValidTab(message.tab)) {
                    container.tile.getTab(message.tab)
                        .setName(message.name);
                    container.tile.tabChanged();
                }
            });
            return null;
        }
    }
}
