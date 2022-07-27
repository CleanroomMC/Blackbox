package com.cleanroommc.blackbox.pipeline.core;

import com.cleanroommc.blackbox.config.category.details.LeavesDetailsConfig;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail.Natural;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail.Placed;
import com.cleanroommc.blackbox.pipeline.core.mixins.ForgeBlockModelRendererAccessor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;

import java.util.List;

public class BlackboxBlockRenderManager extends ForgeBlockModelRenderer {

    private final ThreadLocal<VertexLighterFlat> lighterFlat;
    private final ThreadLocal<VertexLighterSmoothAo> lighterSmooth;
    private final ThreadLocal<VertexBufferConsumer> consumerFlat;
    private final ThreadLocal<VertexBufferConsumer> consumerSmooth;

    public BlackboxBlockRenderManager(BlockColors colors) {
        super(colors);
        ForgeBlockModelRendererAccessor accessor = (ForgeBlockModelRendererAccessor) this;
        this.lighterFlat = accessor.getLighterFlat();
        this.lighterSmooth = accessor.getLighterSmooth();
        this.consumerFlat = accessor.getConsumerFlat();
        this.consumerSmooth = accessor.getConsumerSmooth();
    }

    @Override
    public boolean renderModelFlat(IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, long rand) {
        VertexBufferConsumer consumer = consumerFlat.get();
        consumer.setBuffer(buffer);
        consumer.setOffset(pos);
        VertexLighterFlat lighter = lighterFlat.get();
        lighter.setParent(consumer);
        return render(lighter, world, state, pos, model, checkSides, rand);
    }

    @Override
    public boolean renderModelSmooth(IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder buffer, boolean checkSides, long rand) {
        VertexBufferConsumer consumer = consumerSmooth.get();
        consumer.setBuffer(buffer);
        consumer.setOffset(pos);
        VertexLighterSmoothAo lighter = lighterSmooth.get();
        lighter.setParent(consumer);
        return render(lighter, world, state, pos, model, checkSides, rand);
    }

    private boolean render(VertexLighterFlat lighter, IBlockAccess world, IBlockState state, BlockPos pos, IBakedModel model, boolean checkSides, long rand) {
        lighter.setWorld(world);
        lighter.setState(state);
        lighter.setBlockPos(pos);
        if (state.getBlock() instanceof BlockLeaves) {
            return renderLeaves(lighter, world, state, pos, model, checkSides, rand);
        }
        boolean empty = true;
        List<BakedQuad> quads = model.getQuads(state, null, rand);
        if (!quads.isEmpty()) {
            lighter.updateBlockInfo();
            empty = false;
            for (BakedQuad quad : quads) {
                quad.pipe(lighter);
            }
        }
        for (EnumFacing side : EnumFacing.VALUES) {
            quads = model.getQuads(state, side, rand);
            if (!quads.isEmpty()) {
                if (!checkSides || state.shouldSideBeRendered(world, pos, side)) {
                    if (empty) {
                        lighter.updateBlockInfo();
                    }
                    empty = false;
                    for (BakedQuad quad : quads) {
                        quad.pipe(lighter);
                    }
                }
            }
        }
        lighter.resetBlockInfo();
        return !empty;
    }

    private boolean renderLeaves(VertexLighterFlat lighter, IBlockAccess world, IBlockState state, BlockPos pos, IBakedModel model, boolean checkSides, long rand) {
        boolean empty = true;
        List<BakedQuad> quads = model.getQuads(state, null, rand);
        if (!quads.isEmpty()) {
            lighter.updateBlockInfo();
            empty = false;
            for (BakedQuad quad : quads) {
                quad.pipe(lighter);
            }
        }
        Boolean smart = null;
        for (EnumFacing side : EnumFacing.VALUES) {
            quads = model.getQuads(state, side, rand);
            if (!quads.isEmpty()) {
                if (!checkSides || state.shouldSideBeRendered(world, pos, side)) {
                    if (empty) {
                        lighter.updateBlockInfo();
                    }
                    empty = false;
                    for (BakedQuad quad : quads) {
                        quad.pipe(lighter);
                    }
                } else {
                    if (smart == null) {
                        smart = LeavesDetailsConfig.leavesMode == LeavesDetail.FANCY_SMART ||
                                LeavesDetailsConfig.placedLeavesMode == Placed.FANCY_SMART && !state.getValue(BlockLeaves.DECAYABLE) ||
                                LeavesDetailsConfig.naturalLeavesMode == Natural.FANCY_SMART && state.getValue(BlockLeaves.DECAYABLE);
                    }
                    if (smart) {
                        for (BakedQuad quad : quads) {
                            quad.pipe(lighter);
                        }
                    }
                }
            }
        }
        lighter.resetBlockInfo();
        return !empty;
    }

}
