package industries.angelic.adventuraarcanacore.compat.angelica;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * /lightdebugblock [x y z] [emit [level]]: traces the colored light of the block under the crosshair (or at x y z).
 * With "emit", gives the block's type a light level for this game instead (0 to 15), or toggles it between full and its
 * own; nearby chunks are relit.
 */
final class BlockLightDebug extends ColoredLightDebug.Command {

    @Override
    public String getCommandName() {
        return "lightdebugblock";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lightdebugblock [x y z] [emit [0-15]]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        int x, y, z, side = -1, rest = 0;
        if (args.length >= 3 && isInteger(args[0]) && isInteger(args[1]) && isInteger(args[2])) {
            x = parseInt(sender, args[0]);
            y = parseInt(sender, args[1]);
            z = parseInt(sender, args[2]);
            rest = 3;
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
        if (args.length > rest && args[rest].equals("emit")) {
            String name = Block.blockRegistry.getNameForObject(block);
            Integer level = EmissionOverrides.toggleBlock(
                block,
                world.getBlockMetadata(x, y, z),
                ColoredLightDebug.emitLevel(sender, args, rest + 1));
            ColoredLightDebug.relightAround();
            ColoredLightDebug.say(
                level == null ? name + " gives its own light again (level " + block.getLightValue() + "); chunks relit"
                    : "Every " + name + " gives light level " + level + " this game; chunks relit");
            return;
        }
        ColoredLightDebug.describe(world, x, y, z, side);
        if (EmissionOverrides.overridden(block)) ColoredLightDebug
            .say("Its light level is set to " + block.getLightValue() + " (/lightdebugblock emit to undo)");
        ColoredLightDebug.watch(x, y, z);
    }

    private static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
