package com.cleanroommc.blackbox.pipeline.rendering.quad.blending;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IBlockColor;

import javax.annotation.Nullable;

public interface BlockColorsExtended {

    @Nullable
    IBlockColor getColourInstance(Block block);

}
