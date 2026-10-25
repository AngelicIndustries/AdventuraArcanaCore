package industries.angelic.adventuraarcanacore;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import industries.angelic.adventuraarcanacore.compartment.Compartments;
import industries.angelic.adventuraarcanacore.compartment.TileCompartment;
import industries.angelic.adventuraarcanacore.gui.GuiHandler;
import industries.angelic.adventuraarcanacore.worktable.TileWorktable;
import industries.angelic.adventuraarcanacore.worktable.Worktables;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.loadConfig(event.getSuggestedConfigurationFile());
        if (Config.enableCompartments) Compartments.preInit();
        if (Config.enableWorktable) Worktables.preInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(AdventuraArcanaCore.instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {
        if (Config.enableCompartments) Compartments.registerRecipes();
        if (Config.enableWorktable) Worktables.registerRecipes();
    }

    public void serverStarting(FMLServerStartingEvent event) {}

    public Object createCompartmentGui(EntityPlayer player, TileCompartment tile) {
        return null;
    }

    public Object createWorktableGui(EntityPlayer player, TileWorktable tile) {
        return null;
    }

    public void onConfigReload() {}
}
