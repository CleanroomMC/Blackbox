package com.cleanroommc.blackbox.optimization.core.mixins.signs;

import com.cleanroommc.blackbox.config.category.optimization.HadEnoughSignsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockSign.class)
public abstract class BlockSignMixin extends Block {

    public BlockSignMixin(Material materialIn) {
        super(materialIn);
        throw new AssertionError();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return HadEnoughSignsConfig.enabled ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
    }

}
