package com.cleanroommc.blackbox.opengl.buffer;

import org.lwjgl.opengl.GL15;

public enum GLBufferUsage {

    STREAM_DRAW(GL15.GL_STREAM_DRAW),
    STREAM_READ(GL15.GL_STREAM_READ),
    STREAM_COPY(GL15.GL_STREAM_COPY),
    STATIC_DRAW(GL15.GL_STATIC_DRAW),
    STATIC_READ(GL15.GL_STATIC_READ),
    STATIC_COPY(GL15.GL_STATIC_COPY),
    DYNAMIC_DRAW(GL15.GL_DYNAMIC_DRAW),
    DYNAMIC_READ(GL15.GL_DYNAMIC_READ),
    DYNAMIC_COPY(GL15.GL_DYNAMIC_COPY),
    READ_ONLY(GL15.GL_READ_ONLY),
    WRITE_ONLY(GL15.GL_WRITE_ONLY),
    READ_WRITE(GL15.GL_READ_WRITE);

    public final int id;

    GLBufferUsage(int id) {
        this.id = id;
    }

}
