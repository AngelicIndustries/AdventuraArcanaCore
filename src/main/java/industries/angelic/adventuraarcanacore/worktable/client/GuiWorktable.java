package industries.angelic.adventuraarcanacore.worktable.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import industries.angelic.adventuraarcanacore.gui.CompactButton;
import industries.angelic.adventuraarcanacore.worktable.ContainerWorktable;
import industries.angelic.adventuraarcanacore.worktable.TileWorktable;

public class GuiWorktable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        "adventuraarcanacore",
        "textures/gui/worktable.png");
    private static final ResourceLocation LOCK = new ResourceLocation(
        "adventuraarcanacore",
        "textures/gui/worktable_lock.png");
    private final ContainerWorktable container;
    private final EntityPlayer player;
    private GuiButton previous;
    private GuiButton next;
    private GuiButton source;

    public GuiWorktable(EntityPlayer player, TileWorktable tile) {
        super(new ContainerWorktable(player, tile));
        container = (ContainerWorktable) inventorySlots;
        this.player = player;
        xSize = 176;
        ySize = 218;
    }

    private static String text(String key) {
        return I18n.format("adventuraarcanacore.worktable." + key);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        previous = new CompactButton(ContainerWorktable.PREVIOUS, guiLeft + 74, guiTop + 58, 16, "<");
        next = new CompactButton(ContainerWorktable.NEXT, guiLeft + 92, guiTop + 58, 16, ">");
        source = new GuiButton(ContainerWorktable.SOURCE, guiLeft - 24, guiTop + 20, 20, 20, "");
        buttonList.add(previous);
        buttonList.add(next);
        buttonList.add(source);
        updateButtons();
    }

    private void updateButtons() {
        previous.visible = next.visible = container.conflicts;
        source.displayString = text("source." + container.source + ".short");
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        mc.playerController.sendEnchantPacket(container.windowId, button.id);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (button == 0 && func_146978_c(66, 19, 7, 7, mouseX, mouseY))
            mc.playerController.sendEnchantPacket(container.windowId, ContainerWorktable.CLEAR);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager()
            .bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(I18n.format("tile.adventuraarcanacore.worktable.name"), 8, 6, 0x404040);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, 126, 0x404040);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager()
            .bindTexture(LOCK);
        for (int i = 0; i < 9; i++) if ((container.locks & (1 << i)) != 0)
            func_146110_a(110 + i % 3 * 18, 20 + i / 3 * 18, 0, 0, 16, 16, 16, 16);
        if (!container.craftable && container.getSlot(0)
            .getHasStack()) drawRect(77, 38, 93, 54, 0x55990000);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        super.drawScreen(mouseX, mouseY, partialTick);
        if (func_146978_c(66, 19, 7, 7, mouseX, mouseY))
            drawHoveringText(Arrays.asList(text("clear")), mouseX, mouseY, fontRendererObj);
        if (source.func_146115_a()) drawHoveringText(
            Arrays.asList(text("source." + container.source), text("source.change")),
            mouseX,
            mouseY,
            fontRendererObj);
        for (int i = 0; i < 19; i++) {
            Slot slot = container.getSlot(i);
            if (!func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY)) continue;
            ItemStack stack = slot.getStack();
            List<String> tooltip = stack == null ? new ArrayList<>()
                : new ArrayList<>(stack.getTooltip(player, mc.gameSettings.advancedItemTooltips));
            if (i == 0 && !container.craftable && stack != null) tooltip.add(text("missing"));
            else if (i > 0 && i < 10) tooltip.add(text("ghost"));
            else if (i >= 10 && stack != null) {
                tooltip.add(text("recall"));
                tooltip.add(text((container.locks & (1 << (i - 10))) != 0 ? "unlock" : "lock"));
            }
            if (!tooltip.isEmpty()) drawHoveringText(tooltip, mouseX, mouseY, fontRendererObj);
        }
    }
}
