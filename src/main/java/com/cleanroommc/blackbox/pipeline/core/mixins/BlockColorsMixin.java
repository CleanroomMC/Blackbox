package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.pipeline.rendering.quad.blending.BlockColorsExtended;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(BlockColors.class)
public class BlockColorsMixin implements BlockColorsExtended {

    @Shadow @Final private Map<IRegistryDelegate<Block>, IBlockColor> blockColorMap;

    @Nullable
    @Override
    public IBlockColor getColourInstance(Block block) {
        return this.blockColorMap.get(block.delegate);
    }

}
