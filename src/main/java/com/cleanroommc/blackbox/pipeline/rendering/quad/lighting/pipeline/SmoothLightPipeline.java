package com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.pipeline;

import com.cleanroommc.blackbox.pipeline.rendering.quad.BakedQuadExtension;
import com.cleanroommc.blackbox.pipeline.rendering.quad.BakedQuadFlags;
import com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.LightDataAccess;
import com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.ao.AOFaceData;
import com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.ao.AONeighbourInfo;
import com.cleanroommc.blackbox.util.MathUtil;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.apache.commons.lang3.tuple.Pair;

public class SmoothLightPipeline implements ILightPipeline {

    private static float clamp(float v) {
        if (v < 0.0f) {
            return 0.0f;
        } else if (v > 1.0f) {
            return 1.0f;
        }
        return v;
    }

    private static int getLightMapCoord(float sl, float bl) {
        return (((int) sl & 0xFF) << 16) | ((int) bl & 0xFF);
    }

    private final LightDataAccess lightDataAccess;

    /**
     * The cached face data for each side of a block, both inset and outset.
     */
    private final AOFaceData[] cachedFaceData = new AOFaceData[MathUtil.FACINGS_LENGTH * 2];

    /**
     * The position at which the cached face data was taken at.
     */
    private long cachedPos = Long.MIN_VALUE;

    /**
     * A temporary array for storing the intermediary results of weight data for non-aligned face blending.
     */
    private final float[] weights = new float[4];

    public SmoothLightPipeline(LightDataAccess lightDataAccess) {
        this.lightDataAccess = lightDataAccess;
        for (int i = 0; i < this.cachedFaceData.length; i++) {
            this.cachedFaceData[i] = new AOFaceData();
        }
    }

    @Override
    public void calculate(BakedQuad quad, BlockPos pos, Pair<float[], int[]> out, EnumFacing face, boolean shade) {
        this.updateCachedData(pos.toLong());
        BakedQuadExtension extendedQuad = (BakedQuadExtension) quad;
        int flags = extendedQuad.getFlags();
        final AONeighbourInfo neighborInfo = AONeighbourInfo.get(face);
        // If the model quad is aligned to the block's face and covers it entirely, we can take a fast path and directly
        // map the corner values onto this quad's vertices. This covers most situations during rendering and provides
        // a modest speed-up.
        if (flags == BakedQuadFlags.GRID_ALIGNED) {
            this.applyAlignedFullFace(neighborInfo, pos, face, out, flags);
        } else {
            this.applyComplex(neighborInfo, extendedQuad, pos, face, out, flags);
        }
        this.applySidedBrightness(out, face, shade);
    }

    private void applySidedBrightness(Pair<float[], int[]> out, EnumFacing face, boolean shade) {
        // float brightness = this.lightDataAccess.getWorld().getBrightness(face, shade);
        float brightness = shade ? LightUtil.diffuseLight(face) : 1.0F;
        float[] br = out.getLeft();
        for (int i = 0; i < br.length; i++) {
            br[i] *= brightness;
        }
    }

    private void applyComplex(AONeighbourInfo neighborInfo, BakedQuadExtension quad, BlockPos pos, EnumFacing dir, Pair<float[], int[]> out, int flags) {
        // If the model quad is aligned to the block face, use the corner blocks above this face
        // TODO: is this correct for outset faces? do we even handle that case at all?
        boolean offset = BakedQuadFlags.contains(flags, BakedQuadFlags.GRID_ALIGNED);
        for (int i = 0; i < 4; i++) {
            // Clamp the vertex positions to the block's boundaries to prevent weird errors in lighting
            float cx = clamp(quad.getX(i));
            float cy = clamp(quad.getY(i));
            float cz = clamp(quad.getZ(i));

            float[] weights = this.weights;
            neighborInfo.calculateCornerWeights(cx, cy, cz, weights);

            float depth = neighborInfo.getDepth(cx, cy, cz);

            // If the quad is approximately grid-aligned (not inset), avoid unnecessary computation by treating it is as aligned
            if (MathHelper.epsilonEquals(depth, 0.0F)) {
                this.applyAlignedPartialFace(pos, dir, weights, i, out, offset);
            } else if (MathHelper.epsilonEquals(depth, 1.0F)) {
                this.applyAlignedPartialFace(pos, dir, weights, i, out, offset);
            } else {
                // Blend the occlusion factor between the blocks directly beside this face and the blocks above it
                // based on how inset the face is. This fixes a few issues with blocks such as farmland and paths.
                this.applyInsetPartialFace(pos, dir, depth, 1.0f - depth, weights, i, out);
            }
        }
    }

    private void applyInsetPartialFace(BlockPos pos, EnumFacing dir, float n1d, float n2d, float[] w, int i, Pair<float[], int[]> out) {
        AOFaceData n1 = this.getCachedFaceData(pos, dir, false);
        if (!n1.hasUnpackedLightData()) {
            n1.unpackLightData();
        }
        AOFaceData n2 = this.getCachedFaceData(pos, dir, true);
        if (!n2.hasUnpackedLightData()) {
            n2.unpackLightData();
        }
        // Blend between the direct neighobors and above based on the passed weights
        float ao = (n1.getBlendedShade(w) * n1d) + (n2.getBlendedShade(w) * n2d);
        float sl = (n1.getBlendedSkyLight(w) * n1d) + (n2.getBlendedSkyLight(w) * n2d);
        float bl = (n1.getBlendedBlockLight(w) * n1d) + (n2.getBlendedBlockLight(w) * n2d);
        out.getLeft()[i] = ao;
        out.getRight()[i] = getLightMapCoord(sl, bl);
    }

    /**
     * Calculates the light data for a grid-aligned quad that does not cover the entire block volume's face.
     */
    private void applyAlignedPartialFace(BlockPos pos, EnumFacing dir, float[] w, int i, Pair<float[], int[]> out, boolean offset) {
        AOFaceData faceData = this.getCachedFaceData(pos, dir, offset);
        if (!faceData.hasUnpackedLightData()) {
            faceData.unpackLightData();
        }
        float sl = faceData.getBlendedSkyLight(w);
        float bl = faceData.getBlendedBlockLight(w);
        float ao = faceData.getBlendedShade(w);
        out.getLeft()[i] = ao;
        out.getRight()[i] = getLightMapCoord(sl, bl);
    }

    /**
     * Quickly calculates the light data for a full grid-aligned quad. This represents the most common case (outward
     * facing quads on a full-block model) and avoids interpolation between neighbors as each corner will only ever
     * have two contributing sides.
     */
    private void applyAlignedFullFace(AONeighbourInfo neighborInfo, BlockPos pos, EnumFacing dir, Pair<float[], int[]> out, int flags) {
        AOFaceData faceData = this.getCachedFaceData(pos, dir, BakedQuadFlags.contains(flags, BakedQuadFlags.GRID_ALIGNED));
        neighborInfo.mapCorners(faceData.lm, faceData.ao, out.getRight(), out.getLeft());
    }

    /**
     * Returns the cached data for a given facing or calculates it if it hasn't been cached.
     */
    private AOFaceData getCachedFaceData(BlockPos pos, EnumFacing face, boolean offset) {
        AOFaceData data = this.cachedFaceData[offset ? face.ordinal() : face.ordinal() + 6];
        if (!data.hasLightData()) {
            data.initLightData(this.lightDataAccess, pos, face, offset);
        }
        return data;
    }

    private void updateCachedData(long key) {
        if (this.cachedPos != key) {
            for (AOFaceData data : this.cachedFaceData) {
                data.reset();
            }
            this.cachedPos = key;
        }
    }

}
