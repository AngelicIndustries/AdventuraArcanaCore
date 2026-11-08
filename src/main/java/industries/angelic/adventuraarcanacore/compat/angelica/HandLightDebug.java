package industries.angelic.adventuraarcanacore.compat.angelica;

import java.util.Locale;

import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;

import com.gtnewhorizons.angelica.dynamiclights.DynamicLights;
import com.gtnewhorizons.angelica.dynamiclights.IDynamicLightSource;

/**
 * /lightdebughand [emit [level]]: the held item's dynamic light (its luminance and color, the player's light, and why
 * Angelica's dynamic lights are on or off), then a trace of the block under the player, where that light falls. With
 * "emit", gives the held item (this item and damage) a luminance for this game instead (0 to 15), held or dropped, or
 * toggles it between full and its own.
 */
final class HandLightDebug extends ColoredLightDebug.Command {

    @Override
    public String getCommandName() {
        return "lightdebughand";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lightdebughand [emit [0-15]]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null) {
            sender.addChatMessage(new ChatComponentText("Hold an item."));
            return;
        }
        String name = Item.itemRegistry.getNameForObject(held.getItem());
        if (args.length > 0 && args[0].equals("emit")) {
            Integer level = EmissionOverrides.toggleItem(held, ColoredLightDebug.emitLevel(sender, args, 1));
            ColoredLightDebug.say(
                level == null
                    ? name + " gives its own dynamic light again ("
                        + DynamicLights.getLuminanceFromItemStack(held)
                        + ")"
                    : name + " (damage " + held.getItemDamage() + ") gives dynamic light " + level + " this game");
            return;
        }
        int[] color = DynamicLightColor.color(held);
        IDynamicLightSource player = (IDynamicLightSource) mc.thePlayer;
        ColoredLightDebug.say(
            String.format(
                Locale.ROOT,
                "%s (%s, damage %d): luminance %d%s, color (%d,%d,%d); your light %d %s",
                held.getDisplayName(),
                name,
                held.getItemDamage(),
                DynamicLights.getLuminanceFromItemStack(held),
                EmissionOverrides.item(held) != null ? " (set by /lightdebughand emit)" : "",
                color[0],
                color[1],
                color[2],
                player.angelica$getLuminance(),
                ColoredLightDebug.rgb(player.angelica$getLuminanceRGB())));
        boolean shaders = IrisApi.getInstance()
            .isShaderPackInUse();
        ColoredLightDebug.say(
            String.format(
                Locale.ROOT,
                "Dynamic lights %s: mode %s, forced with shaders %s, shader pack %s; you %s a light source",
                DynamicLights.isEnabled() ? "on" : "off",
                DynamicLights.Mode,
                DynamicLights.ShaderForce ? "on" : "off",
                shaders ? "in use" : "off",
                DynamicLights.get()
                    .containsLightSource(player) ? "are" : "aren't"));
        int x = MathHelper.floor_double(mc.thePlayer.posX), z = MathHelper.floor_double(mc.thePlayer.posZ);
        int y = MathHelper.floor_double(mc.thePlayer.boundingBox.minY) - 1;
        ColoredLightDebug.describe(mc.theWorld, x, y, z, -1);
        ColoredLightDebug.watch(x, y, z);
    }
}
