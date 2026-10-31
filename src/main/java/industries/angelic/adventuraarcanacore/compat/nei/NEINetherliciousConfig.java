package industries.angelic.adventuraarcanacore.compat.nei;

import net.minecraft.item.ItemStack;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import industries.angelic.adventuraarcanacore.Tags;
import industries.angelic.adventuraarcanacore.compat.netherlicious.NetherliciousDuplicates;

/** Hides Netherlicious' copies of Et Futurum's blocks and items. */
public final class NEINetherliciousConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        for (ItemStack stack : NetherliciousDuplicates.duplicates()) API.hideItem(stack);
    }

    @Override
    public String getName() {
        return "Adventura Arcana Core Netherlicious";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
