package industries.angelic.adventuraarcanacore.compartment.client;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import industries.angelic.adventuraarcanacore.compartment.CompartmentNetwork;
import industries.angelic.adventuraarcanacore.compartment.CompartmentSearch;
import industries.angelic.adventuraarcanacore.compartment.CompartmentTab;
import industries.angelic.adventuraarcanacore.compartment.ContainerCompartment;
import industries.angelic.adventuraarcanacore.compartment.TileCompartment;
import industries.angelic.adventuraarcanacore.gui.CompactButton;

public class GuiCompartment extends GuiContainer {

    private static final int CONTROL_ROW_Y = 122;

    private static final int[] COLORS = { 0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00,
        0xAAAAAA, 0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF };
    private final ContainerCompartment container;
    private final TileCompartment tile;
    private final EntityPlayer player;
    private GuiTextField nameField;
    private GuiTextField searchField;
    private boolean includeItems = true;
    private boolean includeBlocks = true;
    private boolean sortByName;
    private int searchPage;
    private int resultCount;

    public GuiCompartment(EntityPlayer player, TileCompartment tile) {
        super(new ContainerCompartment(player.inventory, tile));
        this.player = player;
        this.tile = tile;
        container = (ContainerCompartment) inventorySlots;
        xSize = ContainerCompartment.guiWidth(tile.getTier());
        ySize = 232;
    }

    private static String text(String key) {
        return I18n.format("adventuraarcanacore.compartment." + key);
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        int columns = tile.getTier().pageSize / 5;
        int left = (xSize - columns * 18) / 2;
        int half = tile.getTier().tabs / 2;
        for (int i = 0; i < tile.getTier().tabs; i++) {
            buttonList.add(
                new TabButton(
                    i,
                    guiLeft + (i < half ? left - 26 : left + columns * 18 + 2),
                    guiTop + 26 + i % half * 22));
        }
        buttonList.add(new CompactButton(100, guiLeft + xSize - 48, guiTop + CONTROL_ROW_Y, 40, text("search")));
        buttonList.add(new CompactButton(101, guiLeft + xSize - 48, guiTop + CONTROL_ROW_Y, 40, text("back")));
        buttonList.add(new CompactButton(102, guiLeft + 122, guiTop + 7, 36, text("items")));
        buttonList.add(new CompactButton(103, guiLeft + 160, guiTop + 7, 40, text("blocks")));
        buttonList.add(new CompactButton(104, guiLeft + 202, guiTop + 7, 38, "A-Z"));
        buttonList.add(new CompactButton(105, guiLeft + 8, guiTop + CONTROL_ROW_Y, 20, "<"));
        buttonList.add(new CompactButton(106, guiLeft + 30, guiTop + CONTROL_ROW_Y, 20, ">"));
        nameField = new GuiTextField(fontRendererObj, guiLeft + 8, guiTop + CONTROL_ROW_Y + 2, 90, 12);
        nameField.setMaxStringLength(CompartmentTab.MAX_NAME_LENGTH);
        nameField.setText(
            tile.getTab(container.getCurrentTab())
                .getName());
        searchField = new GuiTextField(fontRendererObj, guiLeft + 8, guiTop + 9, 108, 12);
        searchField.setMaxStringLength(64);
        updateButtons();
    }

    private void updateButtons() {
        boolean search = container.isSearching();
        for (Object entry : buttonList) {
            GuiButton button = (GuiButton) entry;
            button.visible = button.id < tile.getTier().tabs || button.id == 100 ? !search : search;
            button.enabled = true;
            if (button.id == 102) button.displayString = (includeItems ? "\u00a7a" : "\u00a77") + text("items");
            if (button.id == 103) button.displayString = (includeBlocks ? "\u00a7a" : "\u00a77") + text("blocks");
            if (button.id == 104) button.displayString = (sortByName ? "\u00a7a" : "\u00a77") + "A-Z";
            if (button.id == 105) button.enabled = searchPage > 0;
            if (button.id == 106) button.enabled = (searchPage + 1) * tile.getTier().pageSize < resultCount;
        }
    }

    private void action(int action) {
        container.enchantItem(player, action);
        mc.playerController.sendEnchantPacket(container.windowId, action);
    }

    private void saveName() {
        String name = nameField.getText();
        int tab = container.getCurrentTab();
        if (!name.equals(
            tile.getTab(tab)
                .getName())) {
            CompartmentNetwork.CHANNEL.sendToServer(new CompartmentNetwork.Rename(container.windowId, tab, name));
            tile.getTab(tab)
                .setName(name);
        }
        nameField.setFocused(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < tile.getTier().tabs) {
            saveName();
            action(button.id);
            nameField.setText(
                tile.getTab(container.getCurrentTab())
                    .getName());
        } else if (button.id == 100) {
            saveName();
            action(ContainerCompartment.SEARCH);
            searchPage = 0;
            searchField.setFocused(true);
        } else if (button.id == 101) {
            action(ContainerCompartment.CLOSE_SEARCH);
            searchField.setFocused(false);
        } else if (button.id == 102) {
            includeItems = !includeItems;
            searchPage = 0;
        } else if (button.id == 103) {
            includeBlocks = !includeBlocks;
            searchPage = 0;
        } else if (button.id == 104) {
            sortByName = !sortByName;
            searchPage = 0;
        } else if (button.id == 105) {
            searchPage--;
        } else if (button.id == 106) {
            searchPage++;
        }
        if (container.isSearching()) updateSearch();
    }

    private void updateSearch() {
        List<Integer> matches = CompartmentSearch
            .findSlots(tile, searchField.getText(), includeItems, includeBlocks, sortByName);
        resultCount = matches.size();
        int pageSize = tile.getTier().pageSize;
        searchPage = Math.max(0, Math.min(searchPage, Math.max(0, (resultCount - 1) / pageSize)));
        for (int i = 0; i < tile.getSizeInventory(); i++) {
            Slot slot = container.getSlot(i);
            slot.xDisplayPosition = -10000;
            slot.yDisplayPosition = -10000;
        }
        int columns = pageSize / 5;
        int left = (xSize - columns * 18) / 2;
        for (int i = searchPage * pageSize; i < Math.min(matches.size(), (searchPage + 1) * pageSize); i++) {
            Slot slot = container.getSlot(matches.get(i));
            int index = i % pageSize;
            slot.xDisplayPosition = left + index % columns * 18;
            slot.yDisplayPosition = 28 + index / columns * 18;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        nameField.updateCursorCounter();
        searchField.updateCursorCounter();
        if (!nameField.isFocused()) nameField.setText(
            tile.getTab(container.getCurrentTab())
                .getName());
        if (container.isSearching() && !Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) updateSearch();
        updateButtons();
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (nameField.isFocused()) {
            if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER || key == Keyboard.KEY_ESCAPE) saveName();
            else nameField.textboxKeyTyped(character, key);
            return;
        }
        if (container.isSearching() && searchField.isFocused()) {
            if (key == Keyboard.KEY_ESCAPE) {
                searchField.setFocused(false);
                return;
            }
            if (searchField.textboxKeyTyped(character, key)) {
                searchPage = 0;
                updateSearch();
                return;
            }
            // Inventory hotkeys must not close the screen while entering a search.
            if (key == mc.gameSettings.keyBindInventory.getKeyCode()) return;
        }
        super.keyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (!container.isSearching()) {
            boolean wasFocused = nameField.isFocused();
            nameField.mouseClicked(mouseX, mouseY, button);
            if (wasFocused && !nameField.isFocused()) saveName();
            if (nameField.isFocused()) {
                super.mouseClicked(mouseX, mouseY, button);
                return;
            }
            int x = mouseX - guiLeft;
            int y = mouseY - guiTop;
            if (button == 0 && y >= CONTROL_ROW_Y && y < CONTROL_ROW_Y + 16) {
                if (x >= 104 && x < 120) {
                    super.mouseClicked(mouseX, mouseY, button);
                    action(ContainerCompartment.ICON);
                    return;
                }
                if (x >= 124 && x < 188) {
                    super.mouseClicked(mouseX, mouseY, button);
                    action(ContainerCompartment.COLOR + (x - 124) / 8 + (y - CONTROL_ROW_Y) / 8 * 8);
                    return;
                }
            }
        } else {
            searchField.mouseClicked(mouseX, mouseY, button);
            if (searchField.isFocused()) {
                super.mouseClicked(mouseX, mouseY, button);
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (container.isSearching() && wheel != 0) {
            searchPage += wheel < 0 ? 1 : -1;
            updateSearch();
        }
    }

    @Override
    public void onGuiClosed() {
        saveName();
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xFF292929);
        drawRect(guiLeft + 2, guiTop + 2, guiLeft + xSize - 2, guiTop + ySize - 2, 0xFFC6C6C6);
        int columns = tile.getTier().pageSize / 5;
        int left = (xSize - columns * 18) / 2;
        int outline = 0xFF000000 | COLORS[tile.getTab(container.getCurrentTab())
            .getColor()];
        drawRect(guiLeft + left - 3, guiTop + 24, guiLeft + left + columns * 18 + 1, guiTop + 120, outline);
        for (int row = 0; row < 5; row++)
            for (int col = 0; col < columns; col++) drawSlot(guiLeft + left + col * 18, guiTop + 28 + row * 18);
        for (int i = tile.getSizeInventory(); i < container.inventorySlots.size(); i++) {
            Slot slot = container.getSlot(i);
            drawSlot(guiLeft + slot.xDisplayPosition, guiTop + slot.yDisplayPosition);
        }
        if (!container.isSearching()) {
            nameField.drawTextBox();
            drawSlot(guiLeft + 104, guiTop + CONTROL_ROW_Y);
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(
                fontRendererObj,
                mc.getTextureManager(),
                tile.getTab(container.getCurrentTab())
                    .getIcon(),
                guiLeft + 104,
                guiTop + CONTROL_ROW_Y);
            RenderHelper.disableStandardItemLighting();
            for (int i = 0; i < COLORS.length; i++) {
                int x = guiLeft + 124 + i % 8 * 8;
                int y = guiTop + CONTROL_ROW_Y + i / 8 * 8;
                drawRect(x, y, x + 8, y + 8, 0xFF000000);
                drawRect(x + 1, y + 1, x + 7, y + 7, 0xFF000000 | COLORS[i]);
            }
        } else searchField.drawTextBox();
        GL11.glColor4f(1, 1, 1, 1);
    }

    private void drawSlot(int x, int y) {
        drawRect(x - 1, y - 1, x + 17, y + 17, 0xFF373737);
        drawRect(x, y, x + 17, y + 17, 0xFFFFFFFF);
        drawRect(x, y, x + 16, y + 16, 0xFF8B8B8B);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (!container.isSearching()) fontRendererObj.drawString(I18n.format(tile.getInventoryName()), 8, 8, 0x404040);
        else fontRendererObj.drawString(
            I18n.format(
                "adventuraarcanacore.compartment.results",
                resultCount,
                searchPage + 1,
                Math.max(1, (resultCount + tile.getTier().pageSize - 1) / tile.getTier().pageSize)),
            55,
            CONTROL_ROW_Y + 4,
            0x404040);
        fontRendererObj.drawString(I18n.format("container.inventory"), (xSize - 162) / 2, 140, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        super.drawScreen(mouseX, mouseY, partialTick);
        for (Object entry : buttonList) {
            GuiButton button = (GuiButton) entry;
            if (button instanceof TabButton && button.visible && button.func_146115_a()) {
                String name = tile.getTab(button.id)
                    .getName();
                drawHoveringText(
                    Arrays.asList(
                        name.isEmpty() ? I18n.format("adventuraarcanacore.compartment.tab", button.id + 1) : name),
                    mouseX,
                    mouseY,
                    fontRendererObj);
            }
        }
        if (!container.isSearching() && mouseY >= guiTop + CONTROL_ROW_Y && mouseY < guiTop + CONTROL_ROW_Y + 16) {
            int x = mouseX - guiLeft;
            String hint = x >= 8 && x < 98 ? "rename"
                : x >= 104 && x < 120 ? "icon" : x >= 124 && x < 188 ? "color" : "";
            if (!hint.isEmpty()) drawHoveringText(Arrays.asList(text(hint)), mouseX, mouseY, fontRendererObj);
        }
    }

    private class TabButton extends GuiButton {

        TabButton(int id, int x, int y) {
            super(id, x, y, 22, 20, "");
        }

        @Override
        public void drawButton(net.minecraft.client.Minecraft minecraft, int mouseX, int mouseY) {
            if (!visible) return;
            field_146123_n = mouseX >= xPosition && mouseX < xPosition + width
                && mouseY >= yPosition
                && mouseY < yPosition + height;
            drawRect(
                xPosition,
                yPosition,
                xPosition + width,
                yPosition + height,
                0xFF000000 | COLORS[tile.getTab(id)
                    .getColor()]);
            drawRect(
                xPosition + 1,
                yPosition + 1,
                xPosition + width - 1,
                yPosition + height - 1,
                id == container.getCurrentTab() || field_146123_n ? 0xFFDDDDDD : 0xFF888888);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glColor4f(1, 1, 1, 1);
            itemRender.renderItemAndEffectIntoGUI(
                fontRendererObj,
                mc.getTextureManager(),
                tile.getTab(id)
                    .getIcon(),
                xPosition + 3,
                yPosition + 2);
            RenderHelper.disableStandardItemLighting();
        }
    }
}
