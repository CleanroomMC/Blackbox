package io.github.cleanroommc.blackbox.shaders;

import io.github.cleanroommc.blackbox.Blackbox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderLoader;

import java.io.IOException;

public class ShaderLoaders {

	public static ShaderLoader IMAGE_F;
	public static ShaderLoader IMAGE_V;
	public static ShaderLoader SCANNING;
	public static ShaderLoader BLOOM_COMBINE;
	public static ShaderLoader BLUR;
	// Unity
	public static ShaderLoader DOWN_SAMPLING;
	public static ShaderLoader UP_SAMPLING;
	// Unreal
	public static ShaderLoader SEPARABLE_BLUR;
	public static ShaderLoader COMPOSITE;

	public static void init() {
		try {
			IMAGE_F = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "image_f");
			IMAGE_V = load(ShaderLoader.ShaderType.VERTEX, Blackbox.ID, "image_v");
			SCANNING = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "scanning");
			BLOOM_COMBINE = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "bloom_combine");
			BLUR = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "blur");
			DOWN_SAMPLING = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "down_sampling");
			UP_SAMPLING = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "up_sampling");
			SEPARABLE_BLUR = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "separable_blur");
			COMPOSITE = load(ShaderLoader.ShaderType.FRAGMENT, Blackbox.ID, "composite");
		} catch (IOException e) {
			Blackbox.LOGGER.fatal("Could not initialize ShaderLoader!", e);
		}
	}

	public static ShaderLoader load(ShaderLoader.ShaderType shaderType, String domain, String name) throws IOException {
		return ShaderLoader.loadShader(Minecraft.getMinecraft().getResourceManager(), shaderType, domain + ":" + name);
	}

}
