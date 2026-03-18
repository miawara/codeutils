package mia.modmod.features.impl.general;

import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.impl.internal.permissions.Permissions;
import mia.modmod.features.listeners.ModifiableEventData;
import mia.modmod.features.listeners.ModifiableEventResult;
import mia.modmod.features.listeners.impl.ChatEventListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class PlotAdBlocker extends Feature implements ChatEventListener {
    public PlotAdBlocker(Categories category) {
        super(category, "Plot Ad Blocker", "adblocker", "Blocks plot ads.", Permissions.NONE);
    }

    @Override
    public ModifiableEventResult<Component> chatEvent(ModifiableEventData<Component> message, CallbackInfo ci) {
        return message.pass();
    }
}
