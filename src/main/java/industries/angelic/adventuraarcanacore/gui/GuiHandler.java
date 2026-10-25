package industries.angelic.adventuraarcanacore.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.compartment.Compartments;
import industries.angelic.adventuraarcanacore.compartment.ContainerCompartment;
import industries.angelic.adventuraarcanacore.compartment.TileCompartment;
import industries.angelic.adventuraarcanacore.worktable.ContainerWorktable;
import industries.angelic.adventuraarcanacore.worktable.TileWorktable;
import industries.angelic.adventuraarcanacore.worktable.Worktables;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (id == Compartments.GUI_ID && tile instanceof TileCompartment inventory
            && inventory.isUseableByPlayer(player)) return new ContainerCompartment(player.inventory, inventory);
        if (id == Worktables.GUI_ID && tile instanceof TileWorktable table && table.isUseableByPlayer(player))
            return new ContainerWorktable(player, table);
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (id == Compartments.GUI_ID && tile instanceof TileCompartment inventory)
            return AdventuraArcanaCore.proxy.createCompartmentGui(player, inventory);
        if (id == Worktables.GUI_ID && tile instanceof TileWorktable table)
            return AdventuraArcanaCore.proxy.createWorktableGui(player, table);
        return null;
    }
}
