package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.pipeline.core.BlackboxBlockRenderManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockRendererDispatcher.class)
public class BlockRendererDispatcherMixin {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "Lnet/minecraftforge/client/model/pipeline/ForgeBlockModelRenderer;<init>(Lnet/minecraft/client/renderer/color/BlockColors;)V"))
    private ForgeBlockModelRenderer overrideBlockModelRenderer(BlockColors colours) {
        return new BlackboxBlockRenderManager(colours);
    }

}
