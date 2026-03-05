package mia.codeutils.features.impl.development.scanner;

import com.mojang.brigadier.CommandDispatcher;
import mia.codeutils.Mod;
import mia.codeutils.features.Categories;
import mia.codeutils.features.Feature;
import mia.codeutils.features.impl.internal.commands.ChatConsumer;
import mia.codeutils.features.impl.internal.commands.CommandScheduler;
import mia.codeutils.features.impl.internal.commands.ScheduledCommand;
import mia.codeutils.features.impl.internal.mode.LocationAPI;
import mia.codeutils.features.listeners.DFMode;
import mia.codeutils.features.listeners.ModifiableEventData;
import mia.codeutils.features.listeners.ModifiableEventResult;
import mia.codeutils.features.listeners.impl.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public final class PlotScanner extends Feature implements PacketListener, TickEvent, RegisterCommandListener, ChatEventListener, ServerConnectionEventListener {
    public static final int format_version = 1;
    public static final String folder = "plotscan";
    private PlotData plotData;

    private PlotScanningMode plotScanningMode;

    public final Map<Integer, String> idMap = new HashMap<>(Map.ofEntries(
            entry(5000001, "event"),
            entry(5000002, "function"),
            entry(5000003, "process")
    ));

    public PlotScanner(Categories category){
        super(category, "Plot Scanner", "plotscanner", "hit netflix tv show pantheon reference?");
        plotScanningMode = PlotScanningMode.NONE;
    }

    private enum PlotScanningMode {
        NONE,
        REQUESTING_LOCATE_INFO,
        PTP_WAITING
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(ClientCommandManager.literal("plotscan")
                .executes(commandContext -> {
                    if (LocationAPI.getMode().equals(DFMode.DEV)) {
                        if (plotScanningMode.equals(PlotScanningMode.NONE)) {
                            plotScanningMode = PlotScanningMode.REQUESTING_LOCATE_INFO;
                            ScheduledCommand scheduledCommand = new ScheduledCommand("locate", 0L, List.of(
                                    new ChatConsumer(
                                            Pattern.compile("^ {39}\\nYou are currently coding on:\\n\\n→ (.*) \\[([0-9]+)] (?:|\\[.*])\\n→ Owner: ([a-zA-Z0-9_]{3,16}) (?:|§8\\[§7Whitelisted§8])\\n→ Server: (.*)\\n {39}"),
                                            matcher -> {
                                                String plotName = matcher.group(1);
                                                int plotID = Integer.parseInt(matcher.group(2));
                                                String plotOwner = matcher.group(3);
                                                String plotNode = matcher.group(4);
                                                plotData = new PlotData(
                                                        plotOwner,
                                                        plotName,
                                                        plotID,
                                                        plotNode
                                                );
                                                plotScanningMode = PlotScanningMode.PTP_WAITING;
                                                ScheduledCommand positionCommand = new ScheduledCommand("ptp -1 52 0", 0L, List.of());
                                                CommandScheduler.addCommand(positionCommand);
                                            },
                                            () -> {
                                                Mod.messageError("Failed to grab plot id, try again later...");
                                                killScan();
                                            },
                                            10000L,
                                            true
                                    )
                            ));
                            CommandScheduler.addCommand(scheduledCommand);
                        } else {
                            Mod.messageError("Plot scan already in progress, use /killscan to forcefully kill the current scan.");
                        }
                    } else {
                        Mod.messageError("You must be in dev mode to use this feature.");
                    }
                    return 1;
                })
        );

        dispatcher.register(ClientCommandManager.literal("killscan")
                .executes(commandContext -> {
                    if (LocationAPI.getMode().equals(DFMode.DEV)) {
                        if (!plotScanningMode.equals(PlotScanningMode.NONE)) {
                            Mod.message("Killed current plot scan.");
                            killScan();
                        } else {
                            Mod.messageError("No active plot scan to kill.");
                        }
                    } else {
                        Mod.messageError("You must be in dev mode to use this feature.");
                    }
                    return 1;
                })
        );
    }

    private void killScan() {
        plotScanningMode = PlotScanningMode.NONE;
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
    public void tickR(int tick) {

    }

    @Override
    public void tickF(int tick) {

    }

    @Override
    public void serverConnectInit(ClientPacketListener networkHandler, Minecraft minecraftServer) {

    }

    @Override
    public void serverConnectJoin(ClientPacketListener networkHandler, PacketSender sender, Minecraft minecraftServer) {
        plotScanningMode = PlotScanningMode.NONE;
    }

    @Override
    public void serverConnectDisconnect(ClientPacketListener networkHandler, Minecraft minecraftServer) {

    }

    @Override
    public void DFConnectJoin(ClientPacketListener networkHandler) {

    }

    @Override
    public void DFConnectDisconnect(ClientPacketListener networkHandler) {

    }
}
