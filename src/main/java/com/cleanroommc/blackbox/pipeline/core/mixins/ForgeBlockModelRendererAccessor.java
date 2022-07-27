package com.cleanroommc.blackbox.pipeline.core.mixins;

import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ForgeBlockModelRenderer.class, remap = false)
public interface ForgeBlockModelRendererAccessor {

    @Accessor
    ThreadLocal<VertexLighterFlat> getLighterFlat();

    @Accessor
    ThreadLocal<VertexLighterSmoothAo> getLighterSmooth();

    @Accessor
    ThreadLocal<VertexBufferConsumer> getConsumerFlat();

    @Accessor
    ThreadLocal<VertexBufferConsumer> getConsumerSmooth();

}
