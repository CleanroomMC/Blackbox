package com.cleanroommc.blackbox.model.renderer;

import com.cleanroommc.blackbox.model.renderer.mesh.context.RenderContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IRenderer {

    boolean isVanillaLike();

    void emitBlockQuads(IBlockAccess world, IBlockState state, BlockPos pos, long random, RenderContext context);

    void emitItemQuads(ItemStack stack, long random, RenderContext context);

}
