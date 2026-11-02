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

import cpw.mods.fml.client.config.GuiSlider;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;

/**
 * Sliders for the numeric shader options a dimension sets for the current shader pack (/shadertune), to tune them by
 * eye with the world in view. Apply writes them to the dimension's file and reloads the pack. Only values the pack
 * offers for an option can be picked; Reset picks the pack's own default.
 */
public class GuiShaderTuning extends GuiScreen implements GuiSlider.ISlider {

    private static final int APPLY = 0, DONE = 1, FIRST_SLIDER = 100, FIRST_RESET = 200;
    private static final int LEFT = 8, WIDTH = 220, RESET_WIDTH = 40;

    private final File file;
    private final String title;
    private final List<String> names = new ArrayList<>();
    private final List<List<String>> values = new ArrayList<>();
    private final List<Integer> picked = new ArrayList<>();
    private final List<Integer> defaults = new ArrayList<>();
    private final List<GuiSlider> sliders = new ArrayList<>();
    private String status = "";

    public GuiShaderTuning(File file, String dimensionName, String packName, ShaderPack pack) throws IOException {
        this.file = file;
        this.title = I18n.format("adventuraarcanacore.shadertune.title", dimensionName, packName);
        for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
            int equals = line.indexOf('=');
            if (line.trim()
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
        for (int i = 0; i < names.size(); i++) {
            GuiSlider slider = new GuiSlider(
                FIRST_SLIDER + i,
                LEFT,
                24 + i * 22,
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
            buttonList.add(
                new GuiButton(
                    FIRST_RESET + i,
                    LEFT + WIDTH - RESET_WIDTH,
                    24 + i * 22,
                    RESET_WIDTH,
                    20,
                    I18n.format("adventuraarcanacore.shadertune.reset")));
        }
        int y = 28 + names.size() * 22;
        buttonList
            .add(new GuiButton(APPLY, LEFT, y, WIDTH / 2 - 2, 20, I18n.format("adventuraarcanacore.shadertune.apply")));
        buttonList.add(new GuiButton(DONE, LEFT + WIDTH / 2 + 2, y, WIDTH / 2 - 2, 20, I18n.format("gui.done")));
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
        else if (button.id >= FIRST_RESET && button.id < FIRST_RESET + names.size()) {
            int i = button.id - FIRST_RESET;
            // Moves the slider, which picks the value through onChangeSliderValue
            sliders.get(i)
                .setValue(defaults.get(i));
            sliders.get(i)
                .updateSlider();
        }
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
        int bottom = 52 + names.size() * 22 + (status.isEmpty() ? 0 : 12);
        drawRect(LEFT - 4, 4, LEFT + WIDTH + 4, bottom, 0x90000000);
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
