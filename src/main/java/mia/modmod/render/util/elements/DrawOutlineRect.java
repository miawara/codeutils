package mia.modmod.render.util.elements;

import mia.modmod.render.util.ARGB;
import mia.modmod.render.util.DrawContextHelper;
import mia.modmod.render.util.Point;
import net.minecraft.client.gui.GuiGraphics;

public class DrawOutlineRect extends DrawRect {
    public DrawOutlineRect(Point position, Point size, int z, ARGB color) {
        this(position, size, z, color, null);
    }

    public DrawOutlineRect(Point position, Point size, int z, ARGB color, DrawObject parent) {
        super(position, size, z, color, parent);
    }

    @Override
    protected void draw(GuiGraphics context, int mouseX, int mouseY) {
        DrawContextHelper.drawRectBorder(context, x1(), y1(), getWidth(), getHeight(), color);
    }
}
