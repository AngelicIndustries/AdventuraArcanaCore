package industries.angelic.adventuraarcanacore.compat.angelica;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import net.coderbot.iris.Iris;
import net.coderbot.iris.shaderpack.ShaderPack;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.ClientCommandHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/** /shadertune: tunes the shader options the current dimension sets for the current pack (GuiShaderTuning). */
public final class ShaderTuning extends CommandBase {

    /** Shown on the next tick, once the chat that ran the command has closed. */
    private static GuiShaderTuning pending;

    private ShaderTuning() {}

    public static void register() {
        ClientCommandHandler.instance.registerCommand(new ShaderTuning());
        FMLCommonHandler.instance()
            .bus()
            .register(new Opener());
    }

    @Override
    public String getCommandName() {
        return "shadertune";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/shadertune";
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
        Optional<ShaderPack> pack = Iris.getCurrentPack();
        if (!pack.isPresent()) {
            sender.addChatMessage(new ChatComponentTranslation("adventuraarcanacore.shadertune.nopack"));
            return;
        }
        String dimensionName = Iris.getCurrentDimensionName();
        String packName = Iris.getCurrentPackName();
        File file = DimensionShaderOptions.optionsFile(dimensionName, packName);
        if (file == null) {
            sender.addChatMessage(
                new ChatComponentTranslation("adventuraarcanacore.shadertune.nofile", dimensionName, packName));
            return;
        }
        try {
            pending = new GuiShaderTuning(file, dimensionName, packName, pack.get());
        } catch (IOException e) {
            AdventuraArcanaCore.LOG.warn("Couldn't read {}", file, e);
            sender
                .addChatMessage(new ChatComponentTranslation("adventuraarcanacore.shadertune.failed", e.getMessage()));
        }
    }

    public static final class Opener {

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || pending == null) return;
            Minecraft.getMinecraft()
                .displayGuiScreen(pending);
            pending = null;
        }
    }
}
