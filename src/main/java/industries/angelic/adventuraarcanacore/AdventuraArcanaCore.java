package industries.angelic.adventuraarcanacore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = AdventuraArcanaCore.MODID,
    name = "Adventura Arcana Core",
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:unimixins@[0.3.1,);required-after:biomic",
    guiFactory = "industries.angelic.adventuraarcanacore.gui.GuiFactory",
    customProperties = { @Mod.CustomProperty(k = "license", v = "LGPLv3"),
        @Mod.CustomProperty(
            k = "issueTrackerUrl",
            v = "https://github.com/AngelicIndustries/AdventuraArcanaCore/issues"),
        @Mod.CustomProperty(k = "iconFile", v = "assets/adventuraarcanacore/logo.png"),
        @Mod.CustomProperty(k = "backgroundFile", v = "assets/adventuraarcanacore/background.png") })
public class AdventuraArcanaCore {

    public static final String MODID = "adventuraarcanacore";
    public static final String MODGROUP = "industries.angelic";
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final boolean DEBUG_MODE = System.getenv("MCMODDING_DEBUG_MODE") != null;

    @SidedProxy(
        clientSide = MODGROUP + "." + MODID + ".ClientProxy",
        serverSide = MODGROUP + "." + MODID + ".CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    public static void debug(String message) {
        if (DEBUG_MODE || Config.debugMode) {
            LOG.info("DEBUG: {}", message);
        }
    }
}
