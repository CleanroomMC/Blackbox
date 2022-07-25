package com.cleanroommc.blackbox.opengl.buffer;

import com.cleanroommc.blackbox.opengl.GLVersion;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL45;

import java.nio.ByteBuffer;

public enum GLBufferGrowRoutine {

    MAPPING(GLVersion.GL15, (vboId, growFrom, growTo) -> {
        int bridge = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bridge);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, growTo, GLBufferUsage.STREAM_COPY.id);
        ByteBuffer bridgeBuffer = GL15.glMapBuffer(GL15.GL_ARRAY_BUFFER, GL15.GL_READ_WRITE, growFrom, null);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bridgeBuffer, GLBufferUsage.DYNAMIC_DRAW.id);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bridge);
        GL15.glUnmapBuffer(GL15.GL_ARRAY_BUFFER);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(bridge);
    }),
    SUB_DATA(GLVersion.GL31, (vboId, growFrom, growTo) -> {
        int bridge = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bridge);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, growTo, GLBufferUsage.STREAM_COPY.id);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, vboId);
        GL31.glCopyBufferSubData(GL31.GL_COPY_WRITE_BUFFER, GL31.GL_COPY_READ_BUFFER, 0, 0, growFrom);
        GL15.glBufferData(GL31.GL_COPY_WRITE_BUFFER, growTo, GLBufferUsage.DYNAMIC_DRAW.id);
        GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, 0, 0, growFrom);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, 0);
        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, 0);
        GL15.glDeleteBuffers(bridge);
    }),
    NAMED_SUB_DATA(GLVersion.GL45, ((vboId, growFrom, growTo) -> {
        int bridge = GL45.glCreateBuffers();
        GL45.glNamedBufferData(bridge, growFrom, GLBufferUsage.STREAM_COPY.id);
        GL45.glCopyNamedBufferSubData(vboId, bridge, 0, 0, growFrom);
        GL45.glNamedBufferData(vboId, growTo, GLBufferUsage.DYNAMIC_DRAW.id);
        GL45.glCopyNamedBufferSubData(bridge, vboId, 0, 0, growFrom);
        GL15.glDeleteBuffers(bridge);
    }));

    private static GLBufferGrowRoutine suitable;

    public static GLBufferGrowRoutine getSuitable() {
        if (suitable == null) {
            for (GLBufferGrowRoutine mechanism : GLBufferGrowRoutine.values()) {
                if (mechanism.minimum.supported) {
                    suitable = mechanism;
                } else {
                    break;
                }
            }
        }
        return suitable;
    }

    private final GLVersion minimum;
    private final GrowingMechanic mechanic;

    GLBufferGrowRoutine(GLVersion minimum, GrowingMechanic mechanic) {
        this.minimum = minimum;
        this.mechanic = mechanic;
    }

    public GLVersion getMinimumAvailability() {
        return minimum;
    }

    public void growBuffer(int vboId, long growFrom, long growTo) {
        this.mechanic.grow(vboId, growFrom, growTo);
    }

    public void growBuffer(int vboId, int growFrom, int growTo) {
        this.mechanic.grow(vboId, growFrom, growTo);
    }

    @FunctionalInterface
    public interface GrowingMechanic {

        void grow(int vboId, long growFrom, long growTo);

    }

}
