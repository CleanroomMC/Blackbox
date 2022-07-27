package com.cleanroommc.blackbox.keybinding.handler;

import com.cleanroommc.blackbox.config.category.utilities.DynamicZoomConfig;
import com.cleanroommc.blackbox.keybinding.BlackboxKeybinding;
import com.cleanroommc.blackbox.keybinding.IKeybindHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class ZoomHandler implements IKeybindHandler {

    public static final ZoomHandler INSTANCE = new ZoomHandler();

    private float zoomLevel = Float.NEGATIVE_INFINITY;
    private float cachedMouseSensitivity = Float.NEGATIVE_INFINITY;

    private ZoomHandler() { }

    private void init() {
        if (this.zoomLevel < DynamicZoomConfig.defaultZoomLevel) {
            this.zoomLevel = DynamicZoomConfig.defaultZoomLevel;
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (BlackboxKeybinding.ZOOM.isKeyDown()) {
            init();
            int delta = Mouse.getDWheel();
            if (delta > 0) {
                this.zoomLevel *= DynamicZoomConfig.zoomStepping;
            } else if (delta < 0) {
                this.zoomLevel *= 1 - (DynamicZoomConfig.zoomStepping - 1);
            }
            this.zoomLevel = MathHelper.clamp(this.zoomLevel, DynamicZoomConfig.defaultZoomLevel, DynamicZoomConfig.maxZoomLevel);
            event.setCanceled(true);
        } else if (!DynamicZoomConfig.saveZoomLevel) {
            this.zoomLevel = DynamicZoomConfig.defaultZoomLevel;
        }
    }

    @SubscribeEvent
    public void onFoVModify(FOVModifier event) {
        if (BlackboxKeybinding.ZOOM.isKeyDown()) {
            init();
            if (this.cachedMouseSensitivity == Float.NEGATIVE_INFINITY) {
                this.cachedMouseSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
            }
            float adjustedFov = event.getFOV() / this.zoomLevel;
            Minecraft.getMinecraft().gameSettings.mouseSensitivity = this.cachedMouseSensitivity * (adjustedFov / event.getFOV());
            event.setFOV(adjustedFov);
        } else if (this.cachedMouseSensitivity != Float.NEGATIVE_INFINITY) {
            Minecraft.getMinecraft().gameSettings.mouseSensitivity = this.cachedMouseSensitivity;
            this.cachedMouseSensitivity = Float.NEGATIVE_INFINITY;
        }
    }

}
