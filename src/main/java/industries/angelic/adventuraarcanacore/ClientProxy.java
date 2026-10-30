package industries.angelic.adventuraarcanacore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import industries.angelic.adventuraarcanacore.compartment.Compartments;
import industries.angelic.adventuraarcanacore.compartment.TileCompartment;
import industries.angelic.adventuraarcanacore.compartment.client.CompartmentRenderer;
import industries.angelic.adventuraarcanacore.compartment.client.GuiCompartment;
import industries.angelic.adventuraarcanacore.compat.angelica.AngelicaDSWeather;
import industries.angelic.adventuraarcanacore.core.PatchSwitches;
import industries.angelic.adventuraarcanacore.sounds.CaveMoodFilter;
import industries.angelic.adventuraarcanacore.worktable.TileWorktable;
import industries.angelic.adventuraarcanacore.worktable.client.GuiWorktable;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        if (Config.enableCompartments) {
            CompartmentRenderer renderer = new CompartmentRenderer();
            ClientRegistry.bindTileEntitySpecialRenderer(TileCompartment.class, renderer);
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Compartments.BLOCK), renderer);
        }
        MinecraftForge.EVENT_BUS.register(new CaveMoodFilter());
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        if (Loader.isModLoaded("angelica") && Loader.isModLoaded("dsurround")
            && PatchSwitches.enabled("angelicaDsWeather")) AngelicaDSWeather.verify();
    }

    @Override
    public Object createCompartmentGui(EntityPlayer player, TileCompartment tile) {
        return new GuiCompartment(player, tile);
    }

    @Override
    public Object createWorktableGui(EntityPlayer player, TileWorktable tile) {
        return new GuiWorktable(player, tile);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (AdventuraArcanaCore.MODID.equals(event.modID)) {
            Config.sync();
            onConfigReload();
        }
    }
}
