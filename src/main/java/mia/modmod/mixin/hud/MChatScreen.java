package mia.modmod.mixin.hud;

import mia.modmod.features.FeatureManager;
import mia.modmod.features.impl.general.chat.StaffChatComponent;
import mia.modmod.features.impl.general.chat.StaffChatHud;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public class MChatScreen {
    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("HEAD"))
    public void render(GuiGraphics guiGraphics, Font font, int i, int j, int k, boolean bl, boolean bl2, CallbackInfo ci) {
        StaffChatComponent staffChatComponent = FeatureManager.getFeature(StaffChatHud.class).staffChatComponent;
        staffChatComponent.render(guiGraphics, font, i+ 500, j, k, bl, bl2);
    }
}
