package com.cleanroommc.blackbox.shaders.core.mixins;

import net.minecraft.client.shader.ShaderLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShaderLoader.class)
public interface ShaderLoaderExposer {

	@Accessor(value = "shader")
	int blackbox$getShaderId();

}
