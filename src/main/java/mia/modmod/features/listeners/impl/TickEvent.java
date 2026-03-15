package mia.modmod.features.listeners.impl;

import mia.modmod.features.listeners.AbstractEventListener;

public interface TickEvent extends AbstractEventListener {
    void tickR(int tick); // rising edge
    void tickF(int tick); // falling edge
}
