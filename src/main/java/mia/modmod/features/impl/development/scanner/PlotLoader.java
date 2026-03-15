package mia.modmod.features.impl.development.scanner;

import com.mojang.brigadier.CommandDispatcher;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.listeners.ModifiableEventData;
import mia.modmod.features.listeners.ModifiableEventResult;
import mia.modmod.features.listeners.impl.ChatEventListener;
import mia.modmod.features.listeners.impl.PacketListener;
import mia.modmod.features.listeners.impl.RegisterCommandListener;
import mia.modmod.features.listeners.impl.TickEvent;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class PlotLoader extends Feature implements PacketListener, TickEvent, ChatEventListener, RegisterCommandListener {
    public PlotLoader(Categories category) {
        super(category, "Plot Loader", "plotloader", "Load plots from .plot files");
    }

    @Override
    public ModifiableEventResult<Component> chatEvent(ModifiableEventData<Component> message, CallbackInfo ci) {
        return message.pass();
    }

    @Override
    public void receivePacket(Packet<?> packet, CallbackInfo ci) {

    }

    @Override
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {

    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {

    }

    @Override
    public void tickR(int tick) {

    }

    @Override
    public void tickF(int tick) {

    }
}
