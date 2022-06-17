package com.cleanroommc.blackbox.details.core.mixins;

import com.cleanroommc.blackbox.config.category.details.LeavesDetailsConfig;
import com.cleanroommc.blackbox.details.core.LeavesDetail;
import com.cleanroommc.blackbox.details.core.LeavesDetail.Natural;
import com.cleanroommc.blackbox.details.core.LeavesDetail.Placed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockLeaves.class)
public abstract class BlockLeavesMixin extends Block {

    @Shadow @Final public static PropertyBool DECAYABLE;

    protected BlockLeavesMixin(Material materialIn) {
        super(materialIn);
        throw new AssertionError();
    }

    // TODO: side safety
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        if (LeavesDetailsConfig.leavesMode == LeavesDetail.OFF) {
            return state.getValue(DECAYABLE) ? LeavesDetailsConfig.naturalLeavesMode == Natural.FAST : LeavesDetailsConfig.placedLeavesMode == Placed.FAST;
        }
        return LeavesDetailsConfig.leavesMode == LeavesDetail.FAST;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        if (LeavesDetailsConfig.leavesMode == LeavesDetail.OFF) {
            return state.getValue(DECAYABLE) ?
                    (LeavesDetailsConfig.naturalLeavesMode == Natural.FAST ? layer == BlockRenderLayer.SOLID : layer == BlockRenderLayer.CUTOUT_MIPPED) :
                    (LeavesDetailsConfig.placedLeavesMode == Placed.FAST ? layer == BlockRenderLayer.SOLID : layer == BlockRenderLayer.CUTOUT_MIPPED);
        }
        return LeavesDetailsConfig.leavesMode == LeavesDetail.FAST ? layer == BlockRenderLayer.SOLID : layer == BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        switch (LeavesDetailsConfig.leavesMode) {
            case SMART:
                return !(world.getBlockState(pos.offset(side)).getBlock() instanceof BlockLeaves);
            case FAST:
                return !(world.getBlockState(pos.offset(side)).getBlock() == this);
            case FANCY:
                return super.shouldSideBeRendered(state, world, pos, side);
        }
        if (state.getValue(DECAYABLE)) {
            switch (LeavesDetailsConfig.naturalLeavesMode) {
                case SMART:
                    return !(world.getBlockState(pos.offset(side)).getBlock() instanceof BlockLeaves);
                case FAST:
                    return !(world.getBlockState(pos.offset(side)).getBlock() == this);
                case FANCY:
                    return super.shouldSideBeRendered(state, world, pos, side);
            }
        }
        switch (LeavesDetailsConfig.placedLeavesMode) {
            case SMART:
                return !(world.getBlockState(pos.offset(side)).getBlock() instanceof BlockLeaves);
            case FAST:
                return !(world.getBlockState(pos.offset(side)).getBlock() == this);
            default:
                return super.shouldSideBeRendered(state, world, pos, side);
        }
    }

}
