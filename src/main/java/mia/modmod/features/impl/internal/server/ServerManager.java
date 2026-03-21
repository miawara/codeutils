package mia.modmod.features.impl.internal.server;

import mia.modmod.Mod;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.FeatureManager;
import mia.modmod.features.listeners.impl.AlwaysEnabled;
import mia.modmod.features.listeners.impl.PacketListener;
import mia.modmod.features.listeners.impl.ServerConnectionEventListener;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Pattern;

public final class ServerManager extends Feature implements ServerConnectionEventListener, PacketListener, AlwaysEnabled {
    public static RecognizedServers currentServer = RecognizedServers.NONE;
    public static ServerConnectionStatus connectionStatus = ServerConnectionStatus.NONE;

    private static final String[] UNOFFICIAL_ADDRESSES = { "luke.cash", "reason.codes" };
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^(?:(?:(?:.{1,50}\\.|)mcdiamondfire\\.(?:games|net|com|" + String.join("|", UNOFFICIAL_ADDRESSES) + "))|54\\.39\\.29\\.75)(?::[0-9]{1,5}|)$");

    public ServerManager(Categories category) {
        super(category,"Server Manager", "server_manager", "Detects and executes features when you join and leave DF. Will literally brick every other feature if disabled.");

    }

    public static boolean isNotOnDiamondFire() { return !isOnDiamondFire(); }
    public static boolean isOnDiamondFire() { return currentServer.equals(RecognizedServers.DIAMONDFIRE); }

    @Override
    public void serverConnectInit(ClientPacketListener networkHandler, Minecraft minecraftServer) { }

    @Override
    public void serverConnectJoin(ClientPacketListener networkHandler, PacketSender sender, Minecraft minecraftServer) {
        if (networkHandler.getServerData() == null) return;
        RecognizedServers server = ServerManager.recognizeServer(networkHandler.getServerData());
        if (server.equals(RecognizedServers.DIAMONDFIRE) && ServerManager.connectionStatus.equals(ServerConnectionStatus.CONNECTING)) {
            connectionStatus = ServerConnectionStatus.CONNECTED;
            currentServer = RecognizedServers.DIAMONDFIRE;
            Mod.warn("Joined DiamondFire");
            FeatureManager.implementFeatureListener(ServerConnectionEventListener.class, feature -> feature.DFConnectJoin(networkHandler));
        }
    }

    @Override
    public void serverConnectDisconnect(ClientPacketListener networkHandler, Minecraft minecraftServer) {
        if (networkHandler.getServerData() == null) return;
        RecognizedServers server = ServerManager.recognizeServer(networkHandler.getServerData());
        if (server.equals(RecognizedServers.DIAMONDFIRE)) {
            connectionStatus = ServerConnectionStatus.NONE;
            currentServer = RecognizedServers.NONE;
            FeatureManager.implementFeatureListener(ServerConnectionEventListener.class, feature -> feature.DFConnectDisconnect(networkHandler));
        }
    }

    @Override
    public void DFConnectJoin(ClientPacketListener networkHandler) {
        Mod.log("Connected to DiamondFire");
    };

    @Override
    public void DFConnectDisconnect(ClientPacketListener networkHandler) {
        Mod.log("Disconnected from DiamondFire");
    };

    @Override
    public void receivePacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundHelloPacket) {
            connectionStatus = ServerConnectionStatus.CONNECTING;
        }
    }

    @Override
    public void sendPacket(Packet<?> packet, CallbackInfo ci) { }

    public static RecognizedServers recognizeServer(ServerData server) {
        String serverAddress = server.ip;
        if (ADDRESS_PATTERN.matcher(serverAddress).find()) return RecognizedServers.DIAMONDFIRE;
        return RecognizedServers.NONE;
    }
}
