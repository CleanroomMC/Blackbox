package io.github.cleanroommc.blackbox.opengl;

import io.github.cleanroommc.blackbox.Blackbox;
import org.lwjgl.opengl.GLContext;

import java.util.function.BooleanSupplier;

public enum OpenGLVersion {

	GL20("OpenGL 2.0", () -> GLContext.getCapabilities().OpenGL20),
	GL32("OpenGL 3.2", () -> GLContext.getCapabilities().OpenGL32),
	GL40("OpenGL 4.0", () -> GLContext.getCapabilities().OpenGL40),
	GL42("OpenGL 4.2", () -> GLContext.getCapabilities().OpenGL42),
	GL43("OpenGL 4.3", () -> GLContext.getCapabilities().OpenGL43),
	GL44("OpenGL 4.4", () -> GLContext.getCapabilities().OpenGL44),
	GL45("OpenGL 4.5", () -> GLContext.getCapabilities().OpenGL45);

	public final boolean supported;

	OpenGLVersion(String name, BooleanSupplier supplier) {
		boolean supported;
		try {
			supported = supplier.getAsBoolean();
		} catch (Throwable t) {
			Blackbox.LOGGER.error("{} is not supported!", name);
			supported = false;
		}
		this.supported = supported;
	}

}
