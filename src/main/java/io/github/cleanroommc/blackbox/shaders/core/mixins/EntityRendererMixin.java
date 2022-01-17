package io.github.cleanroommc.blackbox.shaders.core.mixins;

import io.github.cleanroommc.blackbox.shaders.ShaderRenderLayer;
import io.github.cleanroommc.blackbox.shaders.depth.DepthHelpers;
import io.github.cleanroommc.blackbox.shaders.management.BlackboxShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
			return ShaderRenderLayer.Bloom.renderLayer(this.mc, renderGlobal, renderLayer, partialTicks, pass, entity);
		}
		return renderGlobal.renderBlockLayer(renderLayer, partialTicks, pass, entity);
	}

	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V", remap = false))
	private void afterRenderingPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
		DepthHelpers.postWorldRender(this.mc, this.mc.renderGlobal, partialTicks);
	}

}
