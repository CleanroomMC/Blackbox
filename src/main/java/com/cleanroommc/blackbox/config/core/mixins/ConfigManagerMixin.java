package com.cleanroommc.blackbox.config.core.mixins;

import com.cleanroommc.blackbox.config.core.RefreshScene;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;

@Mixin(value = ConfigManager.class, remap = false)
public class ConfigManagerMixin {

    @Unique private static boolean needRefreshScene = false;

    @Inject(method = "sync(Ljava/lang/String;Lnet/minecraftforge/common/config/Config$Type;)V", at = @At("TAIL"))
    private static void refreshSceneIfApplicable(String modid, Type type, CallbackInfo ci) {
        if (needRefreshScene) {
            needRefreshScene = false;
            if (Minecraft.getMinecraft().world != null) {
                Minecraft.getMinecraft().renderGlobal.loadRenderers();
            }
        }
    }

    @Inject(method = "sync(Lnet/minecraftforge/common/config/Configuration;Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/Object;)V", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Field;isAnnotationPresent(Ljava/lang/Class;)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void checkRefreshScene(Configuration cfg, Class<?> cls, String modid, String category, boolean loading, Object instance, CallbackInfo ci, Field[] declaredFields, int var7, int var8, Field field) {
        if (!needRefreshScene && field.isAnnotationPresent(RefreshScene.class)) {
            needRefreshScene = true;
        }
    }

}
