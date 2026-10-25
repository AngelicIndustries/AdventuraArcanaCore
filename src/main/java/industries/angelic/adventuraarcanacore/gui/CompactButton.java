package industries.angelic.adventuraarcanacore.gui;

import net.minecraft.client.gui.GuiButton;

public final class CompactButton extends GuiButton {

    public CompactButton(int id, int x, int y, int width, String label) {
        super(id, x, y, width, 16, label);
    }

    @Override
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        // GuiButton draws each half from a 20-pixel texture. Keep its bottom edge when shortening it.
        int top = height / 2;
        int bottom = height - top;
        super.drawTexturedModalRect(x, y, textureX, textureY, width, top);
        super.drawTexturedModalRect(x, y + top, textureX, textureY + 20 - bottom, width, bottom);
    }
}
