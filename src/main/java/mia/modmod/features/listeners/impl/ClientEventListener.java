package mia.modmod.features.listeners.impl;

import mia.modmod.features.listeners.AbstractEventListener;

public interface ClientEventListener extends AbstractEventListener {
    void clientInitialize();
    void clientShutdown();
}
