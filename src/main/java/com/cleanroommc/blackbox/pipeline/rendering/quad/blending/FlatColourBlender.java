package com.cleanroommc.blackbox.pipeline.rendering.quad.blending;

import com.cleanroommc.blackbox.util.ColourHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Arrays;

public class FlatColourBlender implements IColourBlender {

    private final int[] cache = new int[4];

    @Override
    public int[] getColours(IBlockAccess world, BlockPos origin, BakedQuad quad, IBlockColor sampler, IBlockState state) {
        int colour = sampler.colorMultiplier(state, world, origin, quad.getTintIndex());
        if (EntityRenderer.anaglyphEnable) {
            colour = TextureUtil.anaglyphColor(colour);
        }
        Arrays.fill(this.cache, ColourHelper.argbToABGR(colour));
        return this.cache;
    }

}
