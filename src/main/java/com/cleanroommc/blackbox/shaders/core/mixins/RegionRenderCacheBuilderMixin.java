package com.cleanroommc.blackbox.shaders.core.mixins;

import com.cleanroommc.blackbox.shaders.ShaderRenderLayer;
import com.cleanroommc.blackbox.shaders.management.BlackboxShaderManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionRenderCacheBuilder.class)
public class RegionRenderCacheBuilderMixin {

	@Shadow @Final private BufferBuilder[] worldRenderers;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void injectBloomRenderLayer(CallbackInfo ci) {
		if (BlackboxShaderManager.isShadersCompatible()) {
			this.worldRenderers[ShaderRenderLayer.Bloom.LAYER.ordinal()] = new BufferBuilder(131072);
		}
	}

}
