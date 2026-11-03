package industries.angelic.adventuraarcanacore.compat.nei;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.Tags;
import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.Toasts;

public final class NEIToasterConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        if (!Toasts.enabled() || Toasts.recipes()
            .isEmpty()) return;
        if (Toasts.oldToast() != null) API.hideItem(new ItemStack(Toasts.oldToast()));
        ToasterRecipeHandler handler = new ToasterRecipeHandler();
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
        ItemStack toaster = GameRegistry.findItemStack(Toasts.MOD, "toaster", 1);
        if (toaster != null) API.addRecipeCatalyst(toaster, handler);
        MinecraftForge.EVENT_BUS.register(this);
        // NEI loads plugins on entering a world, after its first handler-info event.
        handlerInfo(new NEIRegisterHandlerInfosEvent());
    }

    @SubscribeEvent
    public void handlerInfo(NEIRegisterHandlerInfosEvent event) {
        event.registerHandlerInfo(
            ToasterRecipeHandler.class,
            "Adventura Arcana Core",
            "adventuraarcanacore",
            builder -> builder.setDisplayStack(GameRegistry.findItemStack(Toasts.MOD, "toaster", 1))
                .setHeight(82)
                .setShowOverlayButton(false));
    }

    @Override
    public String getName() {
        return "Adventura Arcana Core toaster";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
