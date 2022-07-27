package com.cleanroommc.blackbox.pipeline.rendering;

import com.cleanroommc.blackbox.pipeline.rendering.quad.BakedQuadExtension;
import com.cleanroommc.blackbox.pipeline.rendering.quad.blending.BlockColorsExtended;
import com.cleanroommc.blackbox.pipeline.rendering.quad.blending.IColourBlender;
import com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.BakedQuadOrientation;
import com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.pipeline.ILightPipeline;
import com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.pipeline.LightPipelineProvider;
import com.cleanroommc.blackbox.util.ColourHelper;
import com.cleanroommc.blackbox.util.MathUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.annotation.Nullable;
import java.util.List;

public class BlackboxBlockRenderer {

    private final boolean useAO;
    private final BlockColorsExtended blockColours;
    private final LightPipelineProvider lighterProvider;
    private final IColourBlender colourBlender;
    private final MutablePair<float[], int[]> lightData = MutablePair.of(new float[4], new int[4]);

    public BlackboxBlockRenderer(BlockColors blockColours, LightPipelineProvider lighterProvider, IColourBlender colourBlender) {
        this.useAO = Minecraft.isAmbientOcclusionEnabled();
        this.blockColours = (BlockColorsExtended) blockColours;
        this.lighterProvider = lighterProvider;
        this.colourBlender = colourBlender;
    }

    /**
     * @see net.minecraft.client.renderer.BlockModelRenderer#renderModel(IBlockAccess, IBakedModel, IBlockState, BlockPos, BufferBuilder, boolean, long)
     */
    public boolean renderModel(IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, long rand) {
        ILightPipeline lighter = this.getLighter(world, model, state, pos);
        boolean rendered = false;
        for (EnumFacing facing : MathUtil.FACINGS) {
            List<BakedQuad> sidedQuads = model.getQuads(state, facing, rand);
            if (!sidedQuads.isEmpty() && (!checkSides || state.shouldSideBeRendered(world, pos, facing))) {
                this.renderQuads(sidedQuads, world, state, pos, buffer, lighter);
                rendered = true;
            }
        }
        List<BakedQuad> genericQuads = model.getQuads(state, null, rand);
        if (!genericQuads.isEmpty()) {
            this.renderQuads(genericQuads, world, state, pos, buffer, lighter);
            rendered = true;
        }
        return rendered;
    }

    private void renderQuads(List<BakedQuad> quads, IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, ILightPipeline lighter) {
        IBlockColor blockColour = null;
        // This is a very hot allocation, iterate over it manually
        for (int i = 0, length = quads.size(); i < length; i++) {
            BakedQuad quad = quads.get(i);
            lighter.calculate(quad, pos, this.lightData, quad.getFace(), quad.shouldApplyDiffuseLighting());
            if (quad.hasTintIndex() && blockColour == null) {
                blockColour = this.blockColours.getColourInstance(state.getBlock());
            }
            renderQuad(quad, world, state, pos, blockColour, buffer);
        }
    }

    private void renderQuad(BakedQuad quad, IBlockAccess world, IBlockState state, BlockPos pos, @Nullable IBlockColor blockColour, BufferBuilder buffer) {
        BakedQuadExtension extendedQuad = (BakedQuadExtension) quad;
        BakedQuadOrientation orientation = BakedQuadOrientation.orientByBrightness(this.lightData.left);
        int[] colours = null;
        if (blockColour != null) {
            colours = this.colourBlender.getColours(world, pos, quad, blockColour, state);
        }
        for (int i = 0; i < 4; i++) {
            int index = orientation.getVertexIndex(i);
            float x = extendedQuad.getX(index);
            float y = extendedQuad.getY(index);
            float z = extendedQuad.getZ(index);
            Vec3d offset = state.getOffset(world, pos);
            if (offset != Vec3d.ZERO) {
                x += offset.x;
                y += offset.y;
                z += offset.z;
            }
            int colour = ColourHelper.ABGR.mul(colours != null ? colours[i] : 0xFFFFFFFF, this.lightData.left[i]);
            float u = extendedQuad.getU(i);
            float v = extendedQuad.getV(i);
            int lightmap = this.lightData.right[i];
            // writeVertex
        }
    }

    private ILightPipeline getLighter(IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos) {
        if (this.useAO && model.isAmbientOcclusion(state) && state.getLightValue(world, pos) == 0) {
            return this.lighterProvider.getSmoothLigher();
        } else {
            return this.lighterProvider.getFlatLighter();
        }
    }

}
