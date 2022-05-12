package com.cleanroommc.blackbox.shaders.core.mixins;

import com.cleanroommc.blackbox.shaders.ShaderRenderLayer;
import com.cleanroommc.blackbox.shaders.ShaderRenderLayer.Bloom;
import com.cleanroommc.blackbox.shaders.depth.DepthHelpers;
import com.cleanroommc.blackbox.shaders.management.BlackboxShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Shadow @Final private Minecraft mc;

	@Inject(method = "renderWorld", at = @At("HEAD"))
	private void beforeRenderingPasses(float partialTicks, long finishTimeNano, CallbackInfo ci) {
		DepthHelpers.preWorldRender(this.mc);
	}

	@Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I", ordinal = 3))
	private int injectShaderLayerRendering(RenderGlobal renderGlobal, BlockRenderLayer renderLayer, double partialTicks, int pass, Entity entity) {
		if (BlackboxShaderManager.isShadersCompatible()) {
			RenderGlobalExposer renderGlobalExposer = (RenderGlobalExposer) renderGlobal;
			List<ContainerLocalRenderInformationAccessor> renderInfos = renderGlobalExposer.getRenderInfos();
			int counts = 0;
			for (ContainerLocalRenderInformationAccessor clri : renderInfos) {
				RenderChunk renderChunk = clri.getRenderChunk();
				if (!renderChunk.getCompiledChunk().isLayerEmpty(Bloom.LAYER)) {
					counts++;
					renderGlobalExposer.getRenderContainer().addRenderChunk(renderChunk, Bloom.LAYER);
				}
			}
			// Avoid doing shaders setup when not applicable
			if (counts > 0) {
				return ShaderRenderLayer.Bloom.renderLayer(this.mc, renderGlobal, partialTicks, pass, entity);
			}
		}
		return renderGlobal.renderBlockLayer(renderLayer, partialTicks, pass, entity);
	}

	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V", remap = false))
	private void afterRenderingPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
		DepthHelpers.postWorldRender(this.mc, this.mc.renderGlobal, partialTicks);
	}

}
