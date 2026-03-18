package mia.modmod.features.impl.moderation.tracker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import mia.modmod.ColorBank;
import mia.modmod.Mod;
import mia.modmod.core.MathUtils;
import mia.modmod.core.StreamUtils;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.FeatureManager;
import mia.modmod.features.impl.internal.permissions.ModeratorPermission;
import mia.modmod.features.impl.internal.permissions.Permissions;
import mia.modmod.features.impl.internal.permissions.SupportPermission;
import mia.modmod.features.impl.internal.server.ServerManager;
import mia.modmod.features.listeners.impl.*;
import mia.modmod.features.parameters.ParameterIdentifier;
import mia.modmod.features.parameters.impl.DoubleDataField;
import mia.modmod.render.util.*;
import mia.modmod.render.util.Point;
import mia.modmod.render.util.elements.DrawRect;
import mia.modmod.render.util.elements.DrawText;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;

import java.awt.*;
import java.util.*;
import java.util.List;

public final class PlayerOutliner extends Feature implements RenderHUD, RegisterCommandListener, ServerConnectionEventListener, WorldRenderEventListener, PacketListener {
    private final ArrayList<String> trackedPlayers;
    private LinkedHashMap<String, Float> playerColors;
    private HashMap<String,ArrayList<Long>> playerClicks;

    private static DoubleDataField timestampSpan;

    public PlayerOutliner(Categories category) {
        super(category, "Player Outliner", "outliner", "outlines tracked players", new Permissions(SupportPermission.NONE, ModeratorPermission.JR_MOD));
        trackedPlayers = new ArrayList<>();
        playerColors = new LinkedHashMap<>();
        playerClicks = new HashMap<>();

        timestampSpan = new DoubleDataField("APS Timespan (seconds)", ParameterIdentifier.of(this, "timespanSpan"), 3.0, true);
    }


    private enum ClickType {
        LEFT,
        RIGHT
    }

    @Override
    public void receivePacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundAnimatePacket entityAnimationS2CPacket) {
            ClientLevel world = Mod.MC.level;
            if (world == null) return;
            Entity entity = world.getEntity(entityAnimationS2CPacket.getId());

            if (entity instanceof Player && entity != Mod.MC.player) {
                Player targetPlayer = (Player) entity;
                String targetPlayerName = targetPlayer.getName().getString();

                ClickType clickType = null;
                if ((entityAnimationS2CPacket.getAction() == ClientboundAnimatePacket.SWING_MAIN_HAND)) {
                    clickType = ClickType.RIGHT;
                } else if (entityAnimationS2CPacket.getAction()  == ClientboundAnimatePacket.SWING_OFF_HAND) {
                    clickType = ClickType.LEFT;
                }
                if (clickType != null) {
                    ArrayList<Long> clicks;
                    if (playerClicks.containsKey(targetPlayerName)) {
                        clicks = playerClicks.get(targetPlayerName);
                    } else {
                        clicks = new ArrayList<>();
                    }
                    clicks.add(System.currentTimeMillis());
                    playerClicks.put(targetPlayerName, clicks);
                }
            }
        }
    }


    @Override
    public void renderHUD(GuiGraphics context, DeltaTracker tickCounter) {
        renderTrackerList(context, tickCounter);
        renderPlayerOutlines(context, tickCounter);
    }

    public ArrayList<String> getTrackedPlayers() {
        return trackedPlayers;
    }

    private void renderTrackerList(GuiGraphics context, DeltaTracker tickCounter) {
        int margin = 5;
        int eachHeight = Mod.MC.font.lineHeight + margin * 2;
        Component titleText = Component.literal("Tracked Players:");
        DrawRect container = new DrawRect(new Point(5,5), new Point(Mod.MC.font.width(titleText.getString()) + margin * 2, eachHeight), 0, new ARGB(ColorBank.BLACK, 0.8f));
        DrawRect containerUnderline = new DrawRect(new Point(0,-1), new Point(container.getWidth(), 1), 0, new ARGB(0xed7aff, 1f),container);
        containerUnderline.setParentBinding(new DrawBinding(AxisBinding.NONE, AxisBinding.FULL));
        DrawText containerTitle = new DrawText(new Point(margin,0), titleText, 0, 1f,true, container);
        containerTitle.setSelfBinding(new DrawBinding(AxisBinding.NONE, AxisBinding.MIDDLE));
        containerTitle.setParentBinding(new DrawBinding(AxisBinding.NONE, AxisBinding.MIDDLE));

        long currentTS = System.currentTimeMillis();

        if (timestampSpan.getValue() <= 0) {
            timestampSpan.setValue(1.0);
            Mod.message(timestampSpan.getName() + " set to double <= 0 -> automatically set to 1.0");
        }
        double seconds = timestampSpan.getValue();
        long period = (long) (seconds * 1000);
        int i = 0;
        for (String player : trackedPlayers) {
            boolean online = StreamUtils.getPlayerList(false).contains(player);
            int onlineColor = online ? ColorBank.MC_GREEN : ColorBank.MC_RED;

            ArrayList<Long> clicks = (ArrayList<Long>) playerClicks.getOrDefault(player, new ArrayList<>()).clone();

            long totalDelta = 0L;

            ArrayList<Long> stamps = new ArrayList<>();
            int numClicks = 0;
            for (long ts : clicks) if (ts + period >= currentTS) {
                long delta = (ts + period) - currentTS;
                stamps.add(ts);
                totalDelta += delta;
                numClicks++;
            }

            double averageDeltaDelta = 0.0;
            if (stamps.size() > 2) {
                long totaldiff = 0L;
                for (int j = 1; j < stamps.size(); j++) {
                    long diff = stamps.get(j) - stamps.get(j-1);
                    totaldiff += diff;
                }
                averageDeltaDelta = ((double) (totaldiff)) / (stamps.size()-1);
            }


            String aps = "μ:" + MathUtils.roundToDecimalPlaces(((double) numClicks) / seconds, 2);
            String sd = "Δ:" + MathUtils.roundToDecimalPlaces(averageDeltaDelta, 1) + "ms";

            Component playerText = Component.literal(player + " ").withColor(0xed7aff).append(Component.literal(aps + " " + sd + " ").withColor(ColorBank.WHITE_GRAY)).append(Component.literal(online ? "online" : "offline").withColor(onlineColor));
            DrawRect playerContainer = new DrawRect(new Point(0,(eachHeight+1) * (i + 1)), new Point(Mod.MC.font.width(playerText.getString()) + (margin + 1) * 2, eachHeight), 0, new ARGB(ColorBank.BLACK, 0.6f), container);
            DrawRect playerContainerSide = new DrawRect(new Point(0,0), new Point(2, playerContainer.getHeight()), 0, new ARGB(onlineColor, 1f), playerContainer);
            DrawText playerTitle = new DrawText(new Point(margin + 2,0), playerText, 0, 1f,false, playerContainer);
            playerTitle.setSelfBinding(new DrawBinding(AxisBinding.NONE, AxisBinding.MIDDLE));
            playerTitle.setParentBinding(new DrawBinding(AxisBinding.NONE, AxisBinding.MIDDLE));
            i++;
        }


        if (!trackedPlayers.isEmpty()) container.render(context, 0, 0);
    }

    private void renderPlayerOutlines(GuiGraphics context, DeltaTracker tickCounter) {
        double currentFov = RenderContextHelper.getFov(tickCounter.getGameTimeDeltaPartialTick(true));

        Matrix4f modelViewMatrix = new Matrix4f()
                .rotationX((float) Math.toRadians(Mod.MC.gameRenderer.getMainCamera().xRot()))
                .rotateY((float) Math.toRadians(Mod.MC.gameRenderer.getMainCamera().yRot() + 180.0F));
        Matrix4f projectionMatrix = Mod.MC.gameRenderer.getProjectionMatrix((float) currentFov);

        Frustum frustum = new Frustum(modelViewMatrix, projectionMatrix);
        Vec3 camPos = Mod.MC.gameRenderer.getMainCamera().position();
        frustum.prepare(camPos.x, camPos.y, camPos.z);

        if (Mod.MC.level == null) return;
        if (Mod.MC.player == null) return;
        if (Mod.MC.getConnection() == null) return;
        if (ServerManager.isNotOnDiamondFire()) return;

        LinkedHashMap<String, Player> nodePlayers = new LinkedHashMap<>();
        for (Player playerEntity : Mod.MC.level.players()) {
            nodePlayers.put(playerEntity.getName().getString(), playerEntity);

            if (playerEntity.getId() != Mod.MC.player.getId() && trackedPlayers.contains(playerEntity.getName().getString())) {
                if (frustum.isVisible(playerEntity.getBoundingBox())) {
                    renderPlayerOutline(context, playerEntity, tickCounter);
                }
            }

        }
    }

    // Need to readd rainbow outline functionality
    public void renderPlayerOutline(GuiGraphics context, Player playerEntity, DeltaTracker tickCounter) {
        ArrayList<Double> xCords = new ArrayList<>();
        ArrayList<Double> yCords = new ArrayList<>();
        List<Vec3> boundingBox = RenderContextHelper.getBoundingBoxCorners(playerEntity);
        for (Vec3 cornerPos : boundingBox) {
            // lerp each corner
            Vec3 screenCornerPos = RenderContextHelper.worldToScreen(cornerPos.subtract(playerEntity.getPosition(tickCounter.getRealtimeDeltaTicks())).add(playerEntity.getPosition(tickCounter.getGameTimeDeltaPartialTick(false))));
            xCords.add(screenCornerPos.x);
            yCords.add(screenCornerPos.y);
        }
        Collections.sort(xCords);
        Collections.sort(yCords);
        AABB screenBoundingBox = new AABB(xCords.getFirst(), yCords.getFirst(), 0, xCords.getLast(), yCords.getLast(), 0);

        int margin = 5;
        int boundingX = (int) screenBoundingBox.minX - margin;
        int boundingY = (int) screenBoundingBox.minY - margin;
        int boundingWidth = (int) (screenBoundingBox.getXsize() + margin * 2);
        int boundingHeight = (int) (screenBoundingBox.getYsize() + margin * 2);


        String playerName = playerEntity.getName().getString();
        if (!playerColors.containsKey(playerName)) playerColors.put(playerName, (float) Math.random());
        int purpleRGB = Color.HSBtoRGB(playerColors.get(playerName), 0.5f , 1f);
        ARGB purple = new ARGB(0xed7aff, 1.0f);
        ARGB fadedPurple = new ARGB(purple.getRGB(), 0.8f);
        boolean isRainbow = false;

        int period = 5;
        long phase = 500L;

        ARGB c1 = isRainbow ? ARGB.getRainbowARGB(0+phase, period) : purple;
        /*
        ARGB c2 = isRainbow ? ARGB.getRainbowARGB(100L+phase, period) : purple;
        ARGB c3 = isRainbow ? ARGB.getRainbowARGB(200L+phase, period) : purple;
        ARGB c4 = isRainbow ? ARGB.getRainbowARGB(300L+phase, period) : purple;
         */

        int x = boundingX;
        int y = boundingY;

        int width = boundingWidth;
        int height = boundingHeight;

        //int z = 100;
        //context.scissorStack.push(new ScreenRect(new ScreenPos(x,y), width, height));
        //context.scissorStack.pop();

        int labelRectMargin = 2;
        int labelRectHeight = Mod.MC.font.lineHeight + labelRectMargin * 2;

        Component labelText = Component.literal(playerEntity.getName().getString()).withColor(ColorBank.WHITE);

        DrawRect labelRect = new DrawRect(new Point(x, y - labelRectHeight), new Point(Mod.MC.font.width(labelText.getString()) + labelRectMargin*2, labelRectHeight), 0, fadedPurple);
        DrawText labelDrawText = new DrawText(new Point(labelRectMargin, 0), labelText, 0, 1f, true, labelRect);
        labelDrawText.setSelfBinding(new DrawBinding(AxisBinding.NONE, AxisBinding.MIDDLE));
        labelDrawText.setParentBinding(new DrawBinding(AxisBinding.NONE, AxisBinding.MIDDLE));

        DrawContextHelper.drawRectBorder(context, x, y, width, height, c1);

        labelRect.render(context, 0, 0);
    }

    public static void addTrackedPlayer(String name) {
        if (!FeatureManager.getFeature(PlayerOutliner.class).trackedPlayers.contains(name)) FeatureManager.getFeature(PlayerOutliner.class).trackedPlayers.add(name);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(ClientCommandManager.literal("track")
                .then(ClientCommandManager.argument("username", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            List<String> list = StreamUtils.getPlayerList(true);
                            list.add("clear");
                            list.addAll(trackedPlayers);
                            return SharedSuggestionProvider.suggest(
                                    list,
                                    builder
                            );
                        })
                        .executes(commandContext -> {
                            String username = StringArgumentType.getString(commandContext, "username");

                            if (username.equals("clear")) {
                                trackedPlayers.clear();
                                Mod.message("Tracker List: Cleared!");
                                return 1;
                            } else {
                                if (trackedPlayers.contains(username)) {
                                    trackedPlayers.remove(username);
                                    Mod.message("Tracker List: Removed " + username);
                                } else {
                                    trackedPlayers.add(username);
                                    Mod.message("Tracker List: Added " + username);
                                }
                            }

                            return 1;
                        })
                )
        );
    }

    @Override
    public void DFConnectJoin(ClientPacketListener networkHandler) {

    }

    @Override
    public void DFConnectDisconnect(ClientPacketListener networkHandler) {
        //trackedPlayers.clear();
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
    public void WorldRenderEvents_END_MAIN(WorldRenderContext context) {

    }

    @Override
    public void WorldRenderEvents_BEFORE_TRANSLUCENT(WorldRenderContext context) {

    }


    @Override
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {

    }

/*


    // :::custom-pipelines:define-pipeline
    private static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder()
            .withLocation(Identifier.of(Mod.MOD_ID, "pipeline/debug_quads"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build()
    );
    // :::custom-pipelines:define-pipeline
    // :::custom-pipelines:extraction-phase
    private static final BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
    private BufferBuilder buffer;

    // :::custom-pipelines:extraction-phase
    // :::custom-pipelines:drawing-phase
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();


 */

}
