package com.cleanroommc.blackbox.pipeline.core.mixins;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ViewFrustum.class)
public interface ViewFrustumAccessor {

    @Invoker
    RenderChunk invokeGetRenderChunk(BlockPos pos);


}
