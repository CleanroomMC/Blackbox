package com.cleanroommc.blackbox.shaders.core.mixins;

import net.minecraft.client.shader.ShaderLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShaderLoader.ShaderType.class)
public interface ShaderTypeExposer {

	@Accessor(value = "shaderMode")
	int blackbox$getShaderMode();

}
