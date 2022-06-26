package com.cleanroommc.blackbox.pipeline.rendering.quad.lighting;

import com.cleanroommc.blackbox.pipeline.rendering.quad.BakedQuadExtension;
import com.cleanroommc.blackbox.pipeline.rendering.quad.flags.BakedQuadFlags;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class FlatLightPipeline implements ILightPipeline {

    private final LightDataAccess lightDataAccess;

    public FlatLightPipeline(LightDataAccess lightDataAccess) {
        this.lightDataAccess = lightDataAccess;
    }

    @Override
    public void calculate(BakedQuad quad, BlockPos pos, Pair<float[], int[]> out, EnumFacing face, boolean shade) {
        if ((((BakedQuadExtension) quad).getFlags() & BakedQuadFlags.GRID_ALIGNED) != 0) { // TODO: && !hasEmissiveLighting
            Arrays.fill(out.getRight(), LightDataAccess.unpackLM(this.lightDataAccess.get(pos, face)));
        } else {
            Arrays.fill(out.getRight(), LightDataAccess.unpackLM(this.lightDataAccess.get(pos)));
        }
        // TODO: change this to account for different dimensions
        Arrays.fill(out.getLeft(), shade ? LightUtil.diffuseLight(face) : 1.0F /*this.lightDataAccess.getWorld().getBrightness()*/);
    }

}
