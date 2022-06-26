package com.cleanroommc.blackbox.pipeline.rendering.quad.lighting;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Thanks to Sodium:
 *
 * Light pipelines allow model quads for any location in the world to be lit using various backends, including fluids and block entities.
 */
public interface ILightPipeline {

    /**
     * Calculates the light data for a given block model quad, storing the result in {@param out}.
     * @param quad The block model quad
     * @param pos The block position of the model this quad belongs to
     * @param out The data arrays which will store the calculated light data results (left: brightness, right: lightmap tex loc)
     * @param face The pre-computed facing vector of the quad
     * @param shade True if the block is shaded by ambient occlusion
     */
    void calculate(BakedQuad quad, BlockPos pos, Pair<float[], float[]> out, EnumFacing face, boolean shade);

}
