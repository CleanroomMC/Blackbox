package com.cleanroommc.blackbox.opengl;

import com.cleanroommc.blackbox.Blackbox;
import org.lwjgl.opengl.GLContext;

import java.util.function.BooleanSupplier;

/**
 * TODO: When Danvil is out, try to support 4.6+ when we move LWJGL to v3
 */
public enum GLVersion {

	GL15("OpenGL 1.5", () -> GLContext.getCapabilities().OpenGL15),
	GL20("OpenGL 2.0", () -> GLContext.getCapabilities().OpenGL20),
	GL21("OpenGL 2.1", () -> GLContext.getCapabilities().OpenGL21),
	GL30("OpenGL 3.0", () -> GLContext.getCapabilities().OpenGL30),
	GL31("OpenGL 3.1", () -> GLContext.getCapabilities().OpenGL31),
	GL32("OpenGL 3.2", () -> GLContext.getCapabilities().OpenGL32),
	GL40("OpenGL 4.0", () -> GLContext.getCapabilities().OpenGL40),
	GL42("OpenGL 4.2", () -> GLContext.getCapabilities().OpenGL42),
	GL43("OpenGL 4.3", () -> GLContext.getCapabilities().OpenGL43),
	GL44("OpenGL 4.4", () -> GLContext.getCapabilities().OpenGL44),
	GL45("OpenGL 4.5", () -> GLContext.getCapabilities().OpenGL45);

	private static GLVersion highestSupportedVersion;

	public static GLVersion getHighestSupportedVersion() {
		if (highestSupportedVersion == null) {
			for (GLVersion version : GLVersion.values()) {
				if (version.supported) {
					highestSupportedVersion = version;
				} else {
					break;
				}
			}
		}
		return highestSupportedVersion;
	}

	public final boolean supported;

	GLVersion(String name, BooleanSupplier supplier) {
		boolean supported;
		try {
			supported = supplier.getAsBoolean();
		} catch (Throwable t) {
			Blackbox.LOGGER.debug("{} is not supported!", name);
			supported = false;
		}
		this.supported = supported;
	}

}
