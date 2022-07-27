package com.cleanroommc.blackbox.pipeline.core.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor {

    @Invoker
    float invokeGetNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks);

}
