package mia.modmod.features.listeners.impl;

import mia.modmod.features.listeners.AbstractEventListener;
import mia.modmod.features.listeners.ModifiableEventData;
import mia.modmod.features.listeners.ModifiableEventResult;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface ChatEventListener extends AbstractEventListener {
    ModifiableEventResult<Component> chatEvent(ModifiableEventData<Component> message, CallbackInfo ci);
}
