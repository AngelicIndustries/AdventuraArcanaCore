package industries.angelic.adventuraarcanacore.compat.supernova;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.common.Loader;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.compat.ItemLightColors;
import industries.angelic.adventuraarcanacore.compat.angelica.DynamicLightColor;

/**
 * Red, green and blue sliders for the light color of a block (/lightcolor, looking at it) or of an item that isn't a
 * block (held). A block's color is Supernova's: Apply writes it to supernova-colors.cfg, registers it and relights the
 * chunks around the player, so placed and held ones change together; an item's goes to itemLightColors, for its dynamic
 * light. Reset takes the entry out and, for a block, puts back the color it had when first opened this game. The game
 * keeps running behind the sliders, to judge the color in place.
 */
public class GuiLightColor extends GuiScreen implements GuiSlider.ISlider {

    private static final int APPLY = 0, RESET = 1, DONE = 2, SCOPE = 3, FIRST_SLIDER = 100;
    private static final int LEFT = 8, WIDTH = 220, TOP = 30, ROW = 22, SWATCH = 42;
    private static final String COLORS_FILE = "supernova-colors.cfg";
    private static final String[] CHANNELS = { "red", "green", "blue" };
    private static final int RELIGHT_RADIUS = 2;
    /** The color each block entry had before this game first changed it, for Reset. */
    private static final Map<String, int[]> originals = new HashMap<>();

    private final String name, title;
    private final int meta, lightValue;
    /** Null for an item that isn't a block. */
    private final Block block;
    private final ItemStack stack;
    private final int[] rgb;
    private final GuiSlider[] sliders = new GuiSlider[3];
    private boolean oneMeta;
    private String status = "";

    private GuiLightColor(String name, int meta, Block block, ItemStack stack, int[] rgb, int lightValue,
        String title) {
        this.name = name;
        this.meta = meta;
        this.block = block;
        this.stack = stack;
        this.rgb = rgb.clone();
        this.lightValue = lightValue;
        this.title = title;
    }

    static GuiLightColor forBlock(Block block, int meta) {
        String name = Block.blockRegistry.getNameForObject(block);
        int[] rgb = SupernovaColors.emission(block, meta);
        GuiLightColor gui = new GuiLightColor(
            name,
            meta,
            block,
            null,
            rgb != null ? rgb : new int[3],
            block.getLightValue(),
            I18n.format("adventuraarcanacore.lightcolor.block", name, meta));
        originals.putIfAbsent(name, gui.rgb.clone());
        originals.putIfAbsent(name + ":" + meta, gui.rgb.clone());
        gui.oneMeta = hasEntry(name + ":" + meta);
        if (!SupernovaColors.available()) gui.status = I18n.format("adventuraarcanacore.lightcolor.nosupernova");
        else if (gui.lightValue == 0) gui.status = I18n.format("adventuraarcanacore.lightcolor.nolight");
        return gui;
    }

    /** A held block item edits its block; any other item its own dynamic light color. */
    static GuiLightColor forItem(ItemStack stack) {
        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (block != null && block != Blocks.air) return forBlock(block, item.getMetadata(stack.getItemDamage()));
        String name = Item.itemRegistry.getNameForObject(item);
        GuiLightColor gui = new GuiLightColor(
            name,
            stack.getItemDamage(),
            null,
            stack,
            DynamicLightColor.color(stack),
            -1,
            I18n.format("adventuraarcanacore.lightcolor.item", stack.getDisplayName(), name));
        gui.oneMeta = ItemLightColors.has(name, stack.getItemDamage());
        if (!Loader.isModLoaded("angelica")) gui.status = I18n.format("adventuraarcanacore.lightcolor.noangelica");
        return gui;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        for (int i = 0; i < 3; i++) {
            sliders[i] = new GuiSlider(
                FIRST_SLIDER + i,
                LEFT,
                TOP + i * ROW,
                WIDTH - SWATCH - 4,
                20,
                I18n.format("adventuraarcanacore.lightcolor." + CHANNELS[i]) + ": ",
                "",
                0,
                15,
                rgb[i],
                false,
                true,
                this);
            buttonList.add(sliders[i]);
        }
        int y = TOP + 3 * ROW;
        buttonList.add(new GuiButton(SCOPE, LEFT, y, WIDTH, 20, scopeLabel()));
        y += ROW + 4;
        int third = (WIDTH - 8) / 3;
        buttonList.add(new GuiButton(RESET, LEFT, y, third, 20, I18n.format("adventuraarcanacore.lightcolor.reset")));
        buttonList.add(
            new GuiButton(APPLY, LEFT + third + 4, y, third, 20, I18n.format("adventuraarcanacore.lightcolor.apply")));
        buttonList
            .add(new GuiButton(DONE, LEFT + 2 * (third + 4), y, WIDTH - 2 * (third + 4), 20, I18n.format("gui.done")));
    }

    private String scopeLabel() {
        return oneMeta ? I18n.format("adventuraarcanacore.lightcolor.onemeta", meta)
            : I18n.format("adventuraarcanacore.lightcolor.allmeta");
    }

    /** The entry's name: supernova-colors.cfg's key for a block, itemLightColors' for an item. */
    private String key() {
        return oneMeta ? name + ":" + meta : name;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == DONE) mc.displayGuiScreen(null);
        else if (button.id == SCOPE) {
            oneMeta = !oneMeta;
            button.displayString = scopeLabel();
        } else if (button.id == APPLY) apply();
        else if (button.id == RESET) reset();
    }

    private void apply() {
        if (block == null) {
            ItemLightColors.set(name, oneMeta ? meta : -1, rgb.clone());
            status = I18n.format("adventuraarcanacore.lightcolor.itemsaved", key());
            return;
        }
        if (!SupernovaColors.available()) return;
        try {
            writeEntry(key(), rgb[0] + ", " + rgb[1] + ", " + rgb[2]);
        } catch (IOException e) {
            AdventuraArcanaCore.LOG.warn("Couldn't write {}", COLORS_FILE, e);
            status = I18n.format("adventuraarcanacore.lightcolor.failed", e.getMessage());
            return;
        }
        SupernovaColors.register(block, oneMeta ? meta : -1, rgb[0], rgb[1], rgb[2]);
        status = I18n.format("adventuraarcanacore.lightcolor.saved", key(), COLORS_FILE);
        relight();
    }

    private void reset() {
        if (block == null) {
            ItemLightColors.set(name, oneMeta ? meta : -1, null);
            System.arraycopy(DynamicLightColor.color(stack), 0, rgb, 0, 3);
            moveSliders();
            status = I18n.format("adventuraarcanacore.lightcolor.itemreset", key());
            return;
        }
        if (!SupernovaColors.available()) return;
        try {
            writeEntry(key(), null);
        } catch (IOException e) {
            AdventuraArcanaCore.LOG.warn("Couldn't write {}", COLORS_FILE, e);
            status = I18n.format("adventuraarcanacore.lightcolor.failed", e.getMessage());
            return;
        }
        int[] original = originals.get(key());
        System.arraycopy(original, 0, rgb, 0, 3);
        moveSliders();
        SupernovaColors.register(block, oneMeta ? meta : -1, rgb[0], rgb[1], rgb[2]);
        status = I18n.format("adventuraarcanacore.lightcolor.reset.done", key());
        relight();
    }

    private void moveSliders() {
        for (int i = 0; i < 3; i++) {
            sliders[i].setValue(rgb[i]);
            sliders[i].updateSlider();
        }
    }

    /** Relights around the player: the client's copy now, the integrated server's on its thread. */
    private void relight() {
        int x = MathHelper.floor_double(mc.thePlayer.posX), z = MathHelper.floor_double(mc.thePlayer.posZ);
        SupernovaColors.relight(mc.theWorld, x, z, RELIGHT_RADIUS);
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            status += " " + I18n.format("adventuraarcanacore.lightcolor.serverkeeps");
            return;
        }
        int dimension = mc.theWorld.provider.dimensionId;
        LightColorCommand.onServer(() -> {
            World world = server.worldServerForDimension(dimension);
            SupernovaColors.relight(world, x, z, RELIGHT_RADIUS);
        });
    }

    /** Sets (or, with a null value, removes) one "key = value" line of supernova-colors.cfg, keeping the rest. */
    private static void writeEntry(String key, String value) throws IOException {
        File file = new File(
            Loader.instance()
                .getConfigDir(),
            COLORS_FILE);
        List<String> lines = file.isFile() ? Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)
            : new ArrayList<>();
        List<String> out = new ArrayList<>();
        for (String line : lines) if (!key.equals(entryKey(line))) out.add(line);
        if (value != null) out.add(key + " = " + value);
        Files.write(file.toPath(), out, StandardCharsets.UTF_8);
    }

    private static boolean hasEntry(String key) {
        File file = new File(
            Loader.instance()
                .getConfigDir(),
            COLORS_FILE);
        if (!file.isFile()) return false;
        try {
            for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8))
                if (key.equals(entryKey(line))) return true;
        } catch (IOException ignored) {}
        return false;
    }

    /** The key of a "key = r, g, b" line, or null for comments and blanks. */
    private static String entryKey(String line) {
        String trimmed = line.trim();
        int equals = trimmed.indexOf('=');
        if (trimmed.isEmpty() || trimmed.startsWith("#") || equals <= 0) return null;
        return trimmed.substring(0, equals)
            .trim();
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        rgb[slider.id - FIRST_SLIDER] = slider.getValueInt();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGradientRect(0, 0, LEFT + WIDTH + 8, height, 0xA0101010, 0x80101010);
        fontRendererObj.drawStringWithShadow(title, LEFT, 8, 0xFFFFFF);
        String info = block == null ? I18n.format("adventuraarcanacore.lightcolor.iteminfo")
            : I18n.format("adventuraarcanacore.lightcolor.blockinfo", lightValue);
        fontRendererObj.drawStringWithShadow(info, LEFT, 18, 0xA0A0A0);
        int max = Math.max(1, Math.max(rgb[0], Math.max(rgb[1], rgb[2])));
        int color = 0xFF000000 | (rgb[0] * 255 / max) << 16 | (rgb[1] * 255 / max) << 8 | rgb[2] * 255 / max;
        int x = LEFT + WIDTH - SWATCH;
        drawRect(x, TOP, LEFT + WIDTH, TOP + 3 * ROW - 2, 0xFF000000);
        drawRect(x + 1, TOP + 1, LEFT + WIDTH - 1, TOP + 3 * ROW - 3, color);
        super.drawScreen(mouseX, mouseY, partialTicks);
        fontRendererObj.drawSplitString(status, LEFT, TOP + 5 * ROW + 10, WIDTH, 0xE0E0E0);
    }
}
