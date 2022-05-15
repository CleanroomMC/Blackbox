package com.cleanroommc.blackbox.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderLoader;

import java.io.IOException;

public class ShaderLoaders {

	public static void init() {
		/*
		try {
		} catch (IOException e) {
			Blackbox.LOGGER.fatal("Could not initialize ShaderLoader!", e);
		}
		 */
	}

	public static ShaderLoader load(ShaderLoader.ShaderType shaderType, String domain, String name) throws IOException {
		return ShaderLoader.loadShader(Minecraft.getMinecraft().getResourceManager(), shaderType, domain + ":" + name);
	}

}
