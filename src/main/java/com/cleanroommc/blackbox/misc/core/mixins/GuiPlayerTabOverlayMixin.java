package com.cleanroommc.blackbox.misc.core.mixins;

import com.cleanroommc.blackbox.config.category.misc.TabOverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin {

    @Shadow @Final private Minecraft mc;

    private static final String MAXIMUM_LATENCY_TO_SHOW = "999+ms";

    @ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE"), ordinal = 7)
    private int changeWidth(int original) {
        return TabOverlayConfig.showNumericalPing ? original + this.mc.fontRenderer.getStringWidth(MAXIMUM_LATENCY_TO_SHOW) : original;
    }

    @Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
    private void onDrawPing(int width, int x, int y, NetworkPlayerInfo networkPlayerInfo, CallbackInfo ci) {
        if (TabOverlayConfig.showNumericalPing) {
            ci.cancel();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int responseTime = networkPlayerInfo.getResponseTime();
            String responseTimeString = responseTime > 999 ? MAXIMUM_LATENCY_TO_SHOW : responseTime + "ms";
            int colour = MathHelper.hsvToRGB(Math.max(0.0F, Math.max(0.0F, (float) responseTime / 500)) / 3.0F, 1.0F, 1.0F);
            this.mc.fontRenderer.drawStringWithShadow(responseTimeString, x + width - this.mc.fontRenderer.getStringWidth(responseTimeString), y, colour);
        }
    }

}
