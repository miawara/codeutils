package mia.modmod.features.impl.general.chat;

import mia.modmod.Mod;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.listeners.ModifiableEventData;
import mia.modmod.features.listeners.ModifiableEventResult;
import mia.modmod.features.listeners.impl.ChatEventListener;
import mia.modmod.features.listeners.impl.TickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.network.chat.Component.literal;

public final class StaffChatHud extends Feature implements TickEvent {
    public StaffChatComponent staffChatComponent;
    public StaffChatHud(Categories category) {
        super(category, "Staff Chat Hud", "staff_chat_hud", "Adds a separate chat hud for staff convos");
    }


    @Override
    public void tickR(int tick) {
        if (Mod.MC.level != null) {
            if (staffChatComponent == null) staffChatComponent = new StaffChatComponent(Mod.MC);
        }
    }

    @Override
    public void tickF(int tick) {

    }
}
