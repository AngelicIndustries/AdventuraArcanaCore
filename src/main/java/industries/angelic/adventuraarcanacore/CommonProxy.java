package industries.angelic.adventuraarcanacore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import industries.angelic.adventuraarcanacore.compartment.Compartments;
import industries.angelic.adventuraarcanacore.compartment.TileCompartment;
import industries.angelic.adventuraarcanacore.compat.biomic.BiomicIntegration;
import industries.angelic.adventuraarcanacore.food.BambooShoots;
import industries.angelic.adventuraarcanacore.food.FoodBlocks;
import industries.angelic.adventuraarcanacore.food.FoodLinks;
import industries.angelic.adventuraarcanacore.food.PamFood;
import industries.angelic.adventuraarcanacore.gui.GuiHandler;
import industries.angelic.adventuraarcanacore.sounds.WolfHowls;
import industries.angelic.adventuraarcanacore.worktable.TileWorktable;
import industries.angelic.adventuraarcanacore.worktable.Worktables;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.loadConfig(event.getSuggestedConfigurationFile());
        if (Config.enableCompartments) Compartments.preInit();
        if (Config.enableWorktable) Worktables.preInit();
        if (Config.enableWildFood) FoodBlocks.register();
        if (Config.enableBambooShoots) BambooShoots.register();
        if (Loader.isModLoaded("biomic")) BiomicIntegration.preInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(AdventuraArcanaCore.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new WolfHowls());
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {
        if (Config.enableCompartments) Compartments.registerRecipes();
        if (Config.enableWorktable) Worktables.registerRecipes();
        if (Config.foodLinks && PamFood.installed()) FoodLinks.register();
        if (Loader.isModLoaded("biomic")) BiomicIntegration.postInit();
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
