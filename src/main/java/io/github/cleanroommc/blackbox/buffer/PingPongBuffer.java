package io.github.cleanroommc.blackbox.buffer;

import net.minecraft.client.shader.Framebuffer;

public class PingPongBuffer {
	
	private static final Framebuffer A;
	private static final Framebuffer B;
	
	private static boolean flag;

	static {
		A = new Framebuffer(10, 10, false);
		B = new Framebuffer(10, 10, false);
		A.setFramebufferColor(0, 0, 0, 0);
		B.setFramebufferColor(0, 0, 0, 0);
	}

	public static void updateSize(int width, int height) {
		BufferHelpers.updateFramebufferSize(A, width, height);
		BufferHelpers.updateFramebufferSize(B, width, height);
	}

	public static void cleanAllUp() {
		A.framebufferClear();
		B.framebufferClear();
	}

	public static Framebuffer getCurrentBuffer(boolean clean) {
		Framebuffer buffer = flag ? A : B;
		if (clean) {
			buffer.framebufferClear();
		}
		return buffer;
	}

	public static Framebuffer getNextBuffer(boolean clean) {
		Framebuffer buffer = flag ? B : A;
		if (clean) {
			buffer.framebufferClear();
		}
		return buffer;
	}

	public static Framebuffer swap() {
		return swap(false);
	}

	public static Framebuffer swap(boolean clean) {
		flag = !flag;
		return getCurrentBuffer(clean);
	}

	public static void bindFramebufferTexture() {
		getCurrentBuffer(false).bindFramebufferTexture();
	}

}