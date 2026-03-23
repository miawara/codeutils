package mia.modmod.mixin.player;

import com.mojang.blaze3d.vertex.PoseStack;
import mia.modmod.features.FeatureManager;
import mia.modmod.features.impl.moderation.tracker.HitRange;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MLivingEntityRenderer{
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("TAIL"))
    private void render(LivingEntityRenderState state, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!(state instanceof AvatarRenderState playerState)) return;
        if (!FeatureManager.getFeature(HitRange.class).getEnabled()) return;
        submitNodeCollector.submitCustomGeometry(matrices, HitRange.QUADS, (entry, vertices) -> HitRange.drawCircle(entry, vertices, playerState));
    }
}