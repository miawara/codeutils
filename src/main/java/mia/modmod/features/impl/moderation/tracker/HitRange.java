package mia.modmod.features.impl.moderation.tracker;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mia.modmod.Mod;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.FeatureManager;
import mia.modmod.features.impl.internal.permissions.ModeratorPermission;
import mia.modmod.features.impl.internal.permissions.Permissions;
import mia.modmod.features.impl.internal.permissions.SupportPermission;
import mia.modmod.features.impl.internal.staff.VanishTracker;
import mia.modmod.mixin.render.RenderTypeAccessor;
import mia.modmod.render.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public final class HitRange extends Feature {
    public HitRange(Categories category) {
        super(category, "Hit Range", "hitrange", "shows player hit range (use this as a reference since its somewhat inaccurate and you can hit outside of the range due to lag)", new Permissions(SupportPermission.NONE, ModeratorPermission.JR_MOD));
    }

    public static void drawCircle(PoseStack.Pose entry, VertexConsumer vertices, AvatarRenderState state) {
        if (Mod.MC.player == null) return;
        if (Mod.MC.level == null) return;

        HashMap<Integer, Player> playerIds = new HashMap<>();
        for (String trackedName : FeatureManager.getFeature(PlayerOutliner.class).getTrackedPlayers()) {
            for (Player player : Mod.MC.level.players()) {
                if (player.nameAndId().name().equals(trackedName)) {
                    playerIds.put(player.getId(), player);
                }
            }
        }

        int playerId = state.id;
        if (!playerIds.containsKey(playerId)) return;

        Player player = playerIds.get(playerId);

        Vec3 playerEyePos = player.getEyePosition();
        float playerRange = (float) player.entityInteractionRange();

        boolean isInRange = false;
        for (Player eachPlayer : Mod.MC.level.players()) {
            if (eachPlayer.getId() != playerId && (!eachPlayer.isSpectator())) {
                if (eachPlayer.getId() == Mod.MC.player.getId() && FeatureManager.getFeature(VanishTracker.class).isInModVanish()) continue;

                AABB boundingBox = eachPlayer.getBoundingBox();
                Vec3 closestPosition = new Vec3(
                        Math.clamp(playerEyePos.x, boundingBox.minX, boundingBox.maxX),
                        Math.clamp(playerEyePos.y, boundingBox.minY, boundingBox.maxY),
                        Math.clamp(playerEyePos.z, boundingBox.minZ, boundingBox.maxZ)
                );
                float distance = (float) closestPosition.distanceTo(playerEyePos);
                if (distance <= playerRange) {
                    isInRange = true;
                    break;
                }
            }
        }

        int color = ARGB.getARGB(isInRange ? 0x7aff5c : 0xff473d, 1f);
        float dy = (state.isDiscrete ? 0.125f : 0);

        drawCircleQuad(
                computeQuads(100, playerRange, 0.05f),
                entry, vertices, dy, color
        );
    }


    private static void drawCircleQuad(ArrayList<Angle> angles, PoseStack.Pose entry, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = entry.pose();
        for (int i = 1; i < angles.size() + 1; i++) {
            Angle angle = angles.get(i % angles.size());
            Angle prevAngle = angles.get(i - 1);

            vertices.addVertex(positionMatrix, prevAngle.dx, dy, prevAngle.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
            vertices.addVertex(positionMatrix, prevAngle.farDx, dy, prevAngle.farDz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
            vertices.addVertex(positionMatrix, angle.farDx, dy, angle.farDz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
            vertices.addVertex(positionMatrix, angle.dx, dy, angle.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
        }
    }

    private static ArrayList<Angle> computeQuads(int segments, float radius, float thickness) {
        ArrayList<Angle> angles = new ArrayList<>();

        for (int i = 0; i < segments; i++) {
            float angle = 2.0f * Mth.PI * ((float) i / segments);
            float dst = radius - (thickness / 2);

            float dx = dst * Mth.sin(angle);
            float dz = dst * Mth.cos(angle);

            float farDx = (dst + thickness) * Mth.sin(angle);
            float farDz = (dst + thickness) * Mth.cos(angle);

            angles.add(new Angle(dx, dz, farDx, farDz));
        }
        return angles;
    }

    private record Angle(float dx, float dz, float farDx, float farDz) { }
}
