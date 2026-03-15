package mia.modmod.features.listeners.impl;

import mia.modmod.features.listeners.AbstractEventListener;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface RenderHUD extends AbstractEventListener {
    void renderHUD(GuiGraphics context, DeltaTracker tickCounter);
}
