package industries.angelic.adventuraarcanacore.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.Config;

public class GuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class ConfigGui extends GuiConfig {

        /** The general settings, then each other category as its own page. */
        private static java.util.List<cpw.mods.fml.client.config.IConfigElement> elements() {
            java.util.List<cpw.mods.fml.client.config.IConfigElement> elements = new java.util.ArrayList<>(
                new ConfigElement<Object>(
                    Config.getRawConfig()
                        .getCategory(Config.Categories.GENERAL)).getChildElements());
            elements.add(
                new ConfigElement<Object>(
                    Config.getRawConfig()
                        .getCategory(Config.Categories.PATCHES)));
            elements.add(
                new ConfigElement<Object>(
                    Config.getRawConfig()
                        .getCategory(Config.Categories.BIOMIC)));
            return elements;
        }

        public ConfigGui(GuiScreen parentScreen) {
            super(
                parentScreen,
                elements(),
                AdventuraArcanaCore.MODID,
                false,
                false,
                I18n.format("adventuraarcanacore.configgui.title"));
        }
    }
}
