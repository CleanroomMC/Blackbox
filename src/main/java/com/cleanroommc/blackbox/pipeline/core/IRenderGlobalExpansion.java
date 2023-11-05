package com.cleanroommc.blackbox.pipeline.core;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;

public interface IRenderGlobalExpansion {

    RenderChunk getRenderChunk(BlockPos pos);

}
