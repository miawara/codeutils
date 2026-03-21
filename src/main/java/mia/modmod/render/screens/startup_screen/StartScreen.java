package mia.modmod.render.screens.startup_screen;

import mia.modmod.ColorBank;
import mia.modmod.Mod;
import mia.modmod.core.MathUtils;
import mia.modmod.features.impl.development.scanner.PlotScanner;
import mia.modmod.render.util.ARGB;
import mia.modmod.render.util.AxisBinding;
import mia.modmod.render.util.DrawBinding;
import mia.modmod.render.util.Point;
import mia.modmod.render.util.elements.DrawButton;
import mia.modmod.render.util.elements.DrawRect;
import mia.modmod.render.util.elements.DrawText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class StartScreen extends Screen {
    private final Screen parent;
    private ArrayList<DrawButton> buttons;

    public StartScreen(Screen parent) {
        super(Component.literal("Startup Screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {

    }

    private void draw(GuiGraphics context, int mouseX, int mouseY) {

    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (parent != null) parent.render(context, Integer.MIN_VALUE, Integer.MIN_VALUE, delta);
        this.renderBlurredBackground(context);

        draw(context, mouseX, mouseY);

        //animation.updateAnimation(0.1f);

        super.render(context, mouseX, mouseY, delta);
    }


    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) { }


    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        for (DrawButton button : buttons) {
            button.mouseClick(click, doubled);
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        return super.charTyped(input);
    }


    @Override
    public boolean keyPressed(KeyEvent input) {
        return super.keyPressed(input);
    }

    @Override
    public void onClose() {
        if (parent == null) Mod.MC.setScreen((Screen) null);
    }
}
