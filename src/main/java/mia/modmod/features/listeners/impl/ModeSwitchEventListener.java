package mia.modmod.features.listeners.impl;

import mia.modmod.features.listeners.AbstractEventListener;
import mia.modmod.features.listeners.DFMode;

public interface ModeSwitchEventListener extends AbstractEventListener {
    void onModeSwitch(DFMode newMode, DFMode previousMode);
}
