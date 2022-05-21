package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.pipeline.core.BlackboxChunkCache;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChunk.class)
public class RenderChunkMixin {

    @Shadow private ChunkCache worldView;

    /**
     * @author Rongmario
     * @reason Use custom ChunkCache
     */
    @Overwrite(remap = false)
    protected ChunkCache createRegionRenderCache(World world, BlockPos from, BlockPos to, int subtract) {
        return new BlackboxChunkCache(world, from, to, subtract);
    }

    @Inject(method = "rebuildChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/CompiledChunk;setVisibility(Lnet/minecraft/client/renderer/chunk/SetVisibility;)V"))
    private void afterMeshing(float x, float y, float z, ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        ((BlackboxChunkCache) this.worldView).clearThreadCaches();
    }

}
