package mia.modmod.core;

import mia.modmod.Mod;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;

public final class NetworkManager {
    public static final ClientPacketListener net = Mod.MC.getConnection();

    public static void sendPacket(Packet<?> packet) {
        if (net == null) return;
        net.send(packet);
    }
}
