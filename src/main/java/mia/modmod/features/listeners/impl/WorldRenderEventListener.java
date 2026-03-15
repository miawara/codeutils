package mia.modmod.features.listeners.impl;

import mia.modmod.features.listeners.AbstractEventListener;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

public interface WorldRenderEventListener extends AbstractEventListener {
    void WorldRenderEvents_END_MAIN(WorldRenderContext context);
    void WorldRenderEvents_BEFORE_TRANSLUCENT(WorldRenderContext context);
}
