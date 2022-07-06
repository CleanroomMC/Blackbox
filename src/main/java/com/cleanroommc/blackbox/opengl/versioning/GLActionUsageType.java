package com.cleanroommc.blackbox.opengl.versioning;

public enum GLActionUsageType {

    GL_STREAM_DRAW(0x88E0),
    GL_STREAM_READ(0x88E1),
    GL_STREAM_COPY(0x88E2),
    GL_STATIC_DRAW(0x88E4),
    GL_STATIC_READ(0x88E5),
    GL_STATIC_COPY(0x88E6),
    GL_DYNAMIC_DRAW(0x88E8),
    GL_DYNAMIC_READ(0x88E9),
    GL_DYNAMIC_COPY(0x88EA),
    GL_READ_ONLY(0x88B8),
    GL_WRITE_ONLY(0x88B9),
    GL_READ_WRITE(0x88BA);

    public final int id;

    GLActionUsageType(int id) {
        this.id = id;
    }

}
