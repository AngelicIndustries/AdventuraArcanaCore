package industries.angelic.adventuraarcanacore;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import industries.angelic.adventuraarcanacore.compartment.Compartments;
import industries.angelic.adventuraarcanacore.compartment.TileCompartment;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.loadConfig(event.getSuggestedConfigurationFile());
        if (Config.enableCompartments) Compartments.preInit();
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {
        if (Config.enableCompartments) Compartments.registerRecipes();
    }

    public void serverStarting(FMLServerStartingEvent event) {}

    public Object createCompartmentGui(EntityPlayer player, TileCompartment tile) {
        return null;
    }

    public void onConfigReload() {}
}
