package com.cleanroommc.blackbox.util;

import com.cleanroommc.blackbox.opengl.buffer.GLBufferUsage;
import org.lwjgl.opengl.GL15;

public class GLHelper {

    public static int genArrayBuffer(int startingSize, GLBufferUsage usage) {
        return genArrayBuffer((long) startingSize, usage, true);
    }

    public static int genArrayBuffer(long startingSize, GLBufferUsage usage) {
        int id = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, startingSize, usage.id);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return id;
    }

    public static int genArrayBuffer(int startingSize, GLBufferUsage usage, boolean unbind) {
        return genArrayBuffer((long) startingSize, usage, unbind);
    }

    public static int genArrayBuffer(long startingSize, GLBufferUsage usage, boolean unbind) {
        int id = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, startingSize, usage.id);
        if (unbind) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
        return id;
    }

    private GLHelper() { }

}
