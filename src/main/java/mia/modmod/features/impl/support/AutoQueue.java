package mia.modmod.features.impl.support;

import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.impl.internal.commands.CommandScheduler;
import mia.modmod.features.impl.internal.commands.ScheduledCommand;
import mia.modmod.features.listeners.impl.ServerConnectionEventListener;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public final class AutoQueue extends Feature implements ServerConnectionEventListener {

    public AutoQueue(Categories category) {
        super(category, "Auto /queue", "autoqueue", "Automatically runs /queue on join.");
    }

    @Override
    public void serverConnectInit(ClientPacketListener networkHandler, Minecraft minecraftServer) {

    }

    @Override
    public void serverConnectJoin(ClientPacketListener networkHandler, PacketSender sender, Minecraft minecraftServer) {

    }

    @Override
    public void serverConnectDisconnect(ClientPacketListener networkHandler, Minecraft minecraftServer) {

    }

    @Override
    public void DFConnectJoin(ClientPacketListener networkHandler) {
        CommandScheduler.addCommand(new ScheduledCommand("queue"));
    }

    @Override
    public void DFConnectDisconnect(ClientPacketListener networkHandler) {

    }
}
