package industries.angelic.adventuraarcanacore.compat.angelica;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.coderbot.iris.shaderpack.ShaderPack;
import net.coderbot.iris.shaderpack.option.MergedStringOption;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.config.GuiSlider;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Sliders for the numeric shader options a dimension sets for the current shader pack (/shadertune), to tune them by
 * eye with the world in view. Apply writes them to the dimension's file and reloads the pack. Only values the pack
 * offers for an option can be picked; Reset picks the pack's own default, Reset all does so for every one. As many as
 * fit the screen show at once, the rest scroll, by wheel or by the bar beside them. A file with a "# /shadertune" line
 * has only the options under it tuned here; the ones above (features a dimension turns off) stay as they are.
 */
public class GuiShaderTuning extends GuiScreen implements GuiSlider.ISlider {

    private static final int APPLY = 0, DONE = 1, RESET_ALL = 2, FIRST_SLIDER = 100, FIRST_RESET = 200;
    private static final String TUNED_BELOW = "# /shadertune";
    private static final int LEFT = 8, WIDTH = 220, RESET_WIDTH = 40;
    private static final int TOP = 24, ROW = 22, BAR = 4;
    /** Room under the rows for Reset all, Apply, Done and the status line. */
    private static final int BELOW = 56;

    private final File file;
    private final String title;
    private final List<String> names = new ArrayList<>();
    private final List<List<String>> values = new ArrayList<>();
    private final List<Integer> picked = new ArrayList<>();
    private final List<Integer> defaults = new ArrayList<>();
    private final List<GuiSlider> sliders = new ArrayList<>();
    private final List<GuiButton> resets = new ArrayList<>();
    private int rows, scroll;
    private boolean dragging;
    private String status = "";

    public GuiShaderTuning(File file, String dimensionName, String packName, ShaderPack pack) throws IOException {
        this.file = file;
        this.title = I18n.format("adventuraarcanacore.shadertune.title", dimensionName, packName);
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        boolean tuned = lines.stream()
            .noneMatch(GuiShaderTuning::isMarker);
        for (String line : lines) {
            if (isMarker(line)) tuned = true;
            int equals = line.indexOf('=');
            if (!tuned || line.trim()
                .startsWith("#") || equals <= 0) continue;
            String name = line.substring(0, equals)
                .trim();
            MergedStringOption option = pack.getShaderPackOptions()
                .getOptionSet()
                .getStringOptions()
                .get(name);
            if (option == null) continue;
            List<String> allowed = new ArrayList<>(
                option.getOption()
                    .getAllowedValues());
            if (allowed.isEmpty() || !allowed.stream()
                .allMatch(GuiShaderTuning::isNumber)) continue;
            allowed.sort(Comparator.comparingDouble(Double::parseDouble));
            names.add(name);
            values.add(allowed);
            picked.add(
                nearest(
                    allowed,
                    line.substring(equals + 1)
                        .trim()));
            defaults.add(
                nearest(
                    allowed,
                    option.getOption()
                        .getDefaultValue()));
        }
    }

    @Override
    public void initGui() {
        buttonList.clear();
        sliders.clear();
        resets.clear();
        rows = Math.min(names.size(), Math.max(1, (height - TOP - BELOW) / ROW));
        for (int i = 0; i < names.size(); i++) {
            GuiSlider slider = new GuiSlider(
                FIRST_SLIDER + i,
                LEFT,
                0,
                WIDTH - RESET_WIDTH - 4,
                20,
                "",
                "",
                0,
                values.get(i)
                    .size() - 1,
                picked.get(i),
                false,
                false,
                this);
            slider.displayString = label(i);
            buttonList.add(slider);
            sliders.add(slider);
            GuiButton reset = new GuiButton(
                FIRST_RESET + i,
                LEFT + WIDTH - RESET_WIDTH,
                0,
                RESET_WIDTH,
                20,
                I18n.format("adventuraarcanacore.shadertune.reset"));
            buttonList.add(reset);
            resets.add(reset);
        }
        scrollTo(scroll);
        int y = TOP + 4 + rows * ROW;
        int third = (WIDTH - 8) / 3;
        buttonList
            .add(new GuiButton(RESET_ALL, LEFT, y, third, 20, I18n.format("adventuraarcanacore.shadertune.resetall")));
        buttonList.add(
            new GuiButton(APPLY, LEFT + third + 4, y, third, 20, I18n.format("adventuraarcanacore.shadertune.apply")));
        buttonList
            .add(new GuiButton(DONE, LEFT + 2 * (third + 4), y, WIDTH - 2 * (third + 4), 20, I18n.format("gui.done")));
    }

    /** Shows the rows from the given one on, as many as fit. */
    private void scrollTo(int first) {
        scroll = Math.max(0, Math.min(names.size() - rows, first));
        for (int i = 0; i < names.size(); i++) {
            boolean shown = i >= scroll && i < scroll + rows;
            int y = TOP + (i - scroll) * ROW;
            sliders.get(i).yPosition = y;
            sliders.get(i).visible = shown;
            resets.get(i).yPosition = y;
            resets.get(i).visible = shown;
        }
    }

    private boolean scrolls() {
        return rows < names.size();
    }

    private int barX() {
        return LEFT + WIDTH + 4;
    }

    private int trackHeight() {
        return rows * ROW - 2;
    }

    private int thumbHeight() {
        return Math.max(8, trackHeight() * rows / names.size());
    }

    private int thumbY() {
        return TOP + (trackHeight() - thumbHeight()) * scroll / (names.size() - rows);
    }

    /** Scrolls so the bar's thumb centres on the mouse. */
    private void dragTo(int mouseY) {
        double along = (mouseY - TOP - thumbHeight() / 2.0) / (trackHeight() - thumbHeight());
        scrollTo((int) Math.round(along * (names.size() - rows)));
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && scrolls()) scrollTo(scroll + (wheel > 0 ? -1 : 1));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (button == 0 && scrolls()
            && mouseX >= barX() - 2
            && mouseX < barX() + BAR + 2
            && mouseY >= TOP
            && mouseY < TOP + trackHeight()) {
            dragging = true;
            dragTo(mouseY);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long held) {
        super.mouseClickMove(mouseX, mouseY, button, held);
        if (dragging) dragTo(mouseY);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if (which != -1) dragging = false;
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        int i = slider.id - FIRST_SLIDER;
        int last = values.get(i)
            .size() - 1;
        picked.set(i, Math.max(0, Math.min(last, (int) Math.round(slider.getValue()))));
        slider.displayString = label(i);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == APPLY) apply();
        else if (button.id == DONE) mc.displayGuiScreen(null);
        else if (button.id == RESET_ALL) for (int i = 0; i < names.size(); i++) reset(i);
        else if (button.id >= FIRST_RESET && button.id < FIRST_RESET + names.size()) reset(button.id - FIRST_RESET);
    }

    /** Moves the slider to the pack's default, which picks the value through onChangeSliderValue. */
    private void reset(int i) {
        sliders.get(i)
            .setValue(defaults.get(i));
        sliders.get(i)
            .updateSlider();
    }

    /** Writes the picked values over the file's lines for them, keeping its comments, and reloads the pack. */
    private void apply() {
        try {
            List<String> lines = new ArrayList<>();
            for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
                int equals = line.indexOf('=');
                int i = equals > 0 && !line.trim()
                    .startsWith("#") ? names.indexOf(
                        line.substring(0, equals)
                            .trim())
                        : -1;
                lines.add(i < 0 ? line : names.get(i) + "=" + picked(i));
            }
            Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            DimensionShaderOptions.reload();
            status = I18n.format("adventuraarcanacore.shadertune.applied", file.getName());
        } catch (IOException | RuntimeException e) {
            AdventuraArcanaCore.LOG.warn("Couldn't apply the shader options in {}", file, e);
            status = I18n.format("adventuraarcanacore.shadertune.failed", e.getMessage());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // No darkening: the world stays in view behind a panel for the controls
        int bottom = 52 + rows * ROW + (status.isEmpty() ? 0 : 12);
        drawRect(LEFT - 4, 4, LEFT + WIDTH + (scrolls() ? BAR + 8 : 4), bottom, 0x90000000);
        if (scrolls()) {
            drawRect(barX(), TOP, barX() + BAR, TOP + trackHeight(), 0x40FFFFFF);
            drawRect(barX(), thumbY(), barX() + BAR, thumbY() + thumbHeight(), 0xC0FFFFFF);
        }
        drawString(fontRendererObj, title, LEFT, 10, 0xFFFFFF);
        if (names.isEmpty())
            drawString(fontRendererObj, I18n.format("adventuraarcanacore.shadertune.none"), LEFT, 30, 0xAAAAAA);
        if (!status.isEmpty()) drawString(fontRendererObj, status, LEFT, bottom - 10, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String picked(int i) {
        return values.get(i)
            .get(picked.get(i));
    }

    private String label(int i) {
        return names.get(i) + ": " + picked(i);
    }

    private static boolean isMarker(String line) {
        return line.trim()
            .equals(TUNED_BELOW);
    }

    private static boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** The index of the allowed value closest to the file's value. */
    private static int nearest(List<String> allowed, String value) {
        double wanted = isNumber(value) ? Double.parseDouble(value) : Double.parseDouble(allowed.get(0));
        int best = 0;
        for (int i = 1; i < allowed.size(); i++) {
            if (Math.abs(Double.parseDouble(allowed.get(i)) - wanted)
                < Math.abs(Double.parseDouble(allowed.get(best)) - wanted)) best = i;
        }
        return best;
    }
}
