package com.cleanroommc.blackbox.pipeline.rendering.quad.blending;

import com.cleanroommc.blackbox.pipeline.rendering.quad.BakedQuadExtension;
import com.cleanroommc.blackbox.util.ColourHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class LinearColourBlender implements IColourBlender {

    private final MutableBlockPos mutablePos = new MutableBlockPos();
    private final int[] cache = new int[4];

    @Override
    public int[] getColours(IBlockAccess world, BlockPos origin, BakedQuad quad, IBlockColor sampler, IBlockState state) {
        for (int vertexIndex = 0; vertexIndex < 4; vertexIndex++) {
            this.cache[vertexIndex] = this.getVertexColour(world, origin, quad, sampler, state, vertexIndex);
        }
        return cache;
    }

    private int getBlockColour(IBlockAccess world, IBlockState state, IBlockColor sampler, int x, int y, int z, int colourIndex) {
        if (EntityRenderer.anaglyphEnable) {
            return TextureUtil.anaglyphColor(sampler.colorMultiplier(state, world, this.mutablePos.setPos(x, y, z), colourIndex));
        }
        return sampler.colorMultiplier(state, world, this.mutablePos.setPos(x, y, z), colourIndex);
    }

    private int getVertexColour(IBlockAccess world, BlockPos origin, BakedQuad quad, IBlockColor sampler, IBlockState state, int vertexIndex) {
        BakedQuadExtension extendedQuad = (BakedQuadExtension) quad;
        // Clamp positions to the range -1.0f to +2.0f to prevent crashes with badly behaved
        // block models and mods causing out-of-bounds array accesses in BiomeColorCache.
        // Offset the position by -0.5f after clamping to align smooth blending with flat blending.
        final float x = MathHelper.clamp(extendedQuad.getX(vertexIndex), -1.0f, 2.0F) - 0.5F;
        final float y = MathHelper.clamp(extendedQuad.getY(vertexIndex), -1.0f, 2.0F) - 0.5F;
        final float z = MathHelper.clamp(extendedQuad.getZ(vertexIndex), -1.0f, 2.0F) - 0.5F;
        // Floor the positions here to always get the largest integer below the input
        // as negative values by default round toward zero when casting to an integer.
        // Which would cause negative ratios to be calculated in the interpolation later on.
        final int intX = (int) Math.floor(x);
        final int intY = (int) Math.floor(y);
        final int intZ = (int) Math.floor(z);
        // Integer component of position vector
        final int originX = origin.getX() + intX;
        final int originY = origin.getY() + intY;
        final int originZ = origin.getZ() + intZ;
        // Retrieve the color values for each neighboring block
        final int c00 = this.getBlockColour(world, state, sampler, originX, originY, originZ, quad.getTintIndex());
        final int c01 = this.getBlockColour(world, state, sampler, originX, originY, originZ + 1, quad.getTintIndex());
        final int c10 = this.getBlockColour(world, state, sampler, originX + 1, originY, originZ, quad.getTintIndex());
        final int c11 = this.getBlockColour(world, state, sampler, originX + 1, originY, originZ + 1, quad.getTintIndex());
        // Fraction component of position vector
        final float fracX = x - intX;
        final float fracZ = z - intZ;
        // Linear interpolation across the Z-axis
        int dz1 = ColourHelper.getStartRatio(fracZ);
        int dz2 = ColourHelper.getEndRatio(fracZ);
        int rz0 = ColourHelper.mixARGB(c00, c01, dz1, dz2);
        int rz1 = ColourHelper.mixARGB(c10, c11, dz1, dz2);
        // Linear interpolation across the X-axis
        int dx1 = ColourHelper.getStartRatio(fracX);
        int dx2 = ColourHelper.getEndRatio(fracX);
        int rx = ColourHelper.mixARGB(rz0, rz1, dx1, dx2);
        return ColourHelper.argbToABGR(rx);
    }

}
