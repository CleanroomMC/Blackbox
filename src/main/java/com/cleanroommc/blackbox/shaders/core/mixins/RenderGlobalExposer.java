package com.cleanroommc.blackbox.shaders.core.mixins;

import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(RenderGlobal.class)
public interface RenderGlobalExposer {

    @Accessor
    ChunkRenderContainer getRenderContainer();

    @Accessor
    List getRenderInfos();

    @Invoker
    void invokeRenderBlockLayer(BlockRenderLayer blockLayerIn);

}
