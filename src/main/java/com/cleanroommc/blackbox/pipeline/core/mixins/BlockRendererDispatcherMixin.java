package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.pipeline.core.BlackboxBlockRenderManager;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.color.BlockColors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRendererDispatcher.class)
public class BlockRendererDispatcherMixin {

    @Shadow @Final @Mutable private BlockModelRenderer blockModelRenderer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void overrideBlockModelRenderer(BlockModelShapes shapes, BlockColors colours, CallbackInfo ci) {
        this.blockModelRenderer = new BlackboxBlockRenderManager(colours);
    }

}
