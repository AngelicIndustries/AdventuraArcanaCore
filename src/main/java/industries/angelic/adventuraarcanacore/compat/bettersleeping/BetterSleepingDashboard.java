package industries.angelic.adventuraarcanacore.compat.bettersleeping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.gtnewhorizons.dashboard.api.HudBounds;
import com.gtnewhorizons.dashboard.api.HudElement;
import com.gtnewhorizons.dashboard.api.HudRegistry;
import com.gtnewhorizons.dashboard.api.HudShift;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cz.ondraster.bettersleeping.Config;
import cz.ondraster.bettersleeping.item.ItemClass;
import cz.ondraster.bettersleeping.logic.MinecraftTime;

/**
 * Better Sleeping's sleep bar and Ring Watch time as elements of Dashboard's HUD editor (betterSleepingDashboard),
 * moved through MixinSleepOverlayDashboard. The only class using Dashboard's API, so it is only loaded with Dashboard.
 */
@SideOnly(Side.CLIENT)
public final class BetterSleepingDashboard {

    private static final ResourceLocation BAR = new ResourceLocation("bettersleeping", "textures/gui/bar.png");

    /** Noon, for the Ring Watch's sample in the editor. */
    private static final long SAMPLE_TIME = 6000;

    /** The bar is 32 by 8; the bed beside it is 16 square, 4 to the right of the bar and 4 higher. */
    private static final HudElement SLEEP_BAR = new HudElement(
        "bettersleeping:sleep_bar",
        (screenWidth, screenHeight) -> new HudBounds(Config.guiOffsetLeft, Config.guiOffsetTop - 4, 52, 16));

    /** Better Sleeping centers the time 16 to the right of the bar's left edge, 16 below its top. */
    private static final HudElement RING_WATCH = new HudElement(
        "bettersleeping:ring_watch",
        (screenWidth, screenHeight) -> {
            int width = timeWidth();
            return new HudBounds(Config.guiOffsetLeft + 16 - width / 2, Config.guiOffsetTop + 16, width, 8);
        });

    /** The bar is drawn in the same method as other things, so a hidden one is drawn invisibly, not skipped. */
    private static final HudShift BAR_SHIFT = new HudShift();

    private BetterSleepingDashboard() {}

    public static void register() {
        SLEEP_BAR.setPreview(
            (screenWidth, screenHeight) -> HudRegistry.render(SLEEP_BAR, BetterSleepingDashboard::drawSampleBar));
        RING_WATCH.setActiveCheck(() -> Config.enableRingWatch && Loader.isModLoaded("Baubles"))
            .setPreview(
                (screenWidth, screenHeight) -> HudRegistry
                    .render(RING_WATCH, () -> drawTime(MinecraftTime.getFromWorldTime(SAMPLE_TIME))));
        HudRegistry.register(SLEEP_BAR);
        HudRegistry.register(RING_WATCH);
    }

    public static void beginBar() {
        BAR_SHIFT.beginOrHide(SLEEP_BAR);
    }

    public static void endBar() {
        BAR_SHIFT.end();
    }

    /** False when the player hid the time, so it isn't drawn. Only asked while the watch is worn. */
    public static boolean beginWatch() {
        return HudRegistry.beginRender(RING_WATCH);
    }

    public static void endWatch() {
        HudRegistry.endRender();
    }

    /** Whether Better Sleeping is about to draw the time: the player wears the Ring Watch as a bauble. */
    public static boolean wearsRingWatch() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        IInventory baubles = player == null ? null : BaublesApi.getBaubles(player);
        if (baubles == null) {
            return false;
        }
        for (int slot = 0; slot < baubles.getSizeInventory(); slot++) {
            ItemStack stack = baubles.getStackInSlot(slot);
            if (stack != null && stack.getItem() == ItemClass.itemRingWatch) {
                return true;
            }
        }
        return false;
    }

    private static int timeWidth() {
        Minecraft mc = Minecraft.getMinecraft();
        long time = mc.theWorld != null ? mc.theWorld.getWorldTime() : SAMPLE_TIME;
        return mc.fontRenderer.getStringWidth(
            MinecraftTime.getFromWorldTime(time)
                .toString());
    }

    /** The bar a third full, as Better Sleeping draws it. */
    private static void drawSampleBar() {
        Minecraft mc = Minecraft.getMinecraft();
        Gui gui = new Gui();
        mc.renderEngine.bindTexture(BAR);
        gui.drawTexturedModalRect(Config.guiOffsetLeft, Config.guiOffsetTop, 0, 0, 32, 8);
        gui.drawTexturedModalRect(Config.guiOffsetLeft + 8, Config.guiOffsetTop, 0, 8, 8, 8);
        mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
        ItemStack bed = new ItemStack(Items.bed);
        gui.drawTexturedModelRectFromIcon(
            Config.guiOffsetLeft + 32 + 4,
            Config.guiOffsetTop - 4,
            Items.bed.getIcon(bed, 1),
            16,
            16);
    }

    private static void drawTime(MinecraftTime time) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        new Gui()
            .drawCenteredString(font, time.toString(), Config.guiOffsetLeft + 16, Config.guiOffsetTop + 16, 0xFFFFFF);
    }
}
