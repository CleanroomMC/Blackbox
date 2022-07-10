package com.cleanroommc.blackbox.opengl.buffer;

import com.cleanroommc.blackbox.opengl.GLVersion;
import org.lwjgl.opengl.*;

public enum GLBuffer {

    ARRAY(GLVersion.GL15, GL15.GL_ARRAY_BUFFER),
    ELEMENT_ARRAY(GLVersion.GL15, GL15.GL_ELEMENT_ARRAY_BUFFER),
    PIXEL_PACK(GLVersion.GL21, GL21.GL_PIXEL_PACK_BUFFER),
    PIXEL_UNPACK(GLVersion.GL21, GL21.GL_PIXEL_UNPACK_BUFFER),
    TRANSFORM_FEEDBACK(GLVersion.GL30, GL30.GL_TRANSFORM_FEEDBACK_BUFFER),
    UNIFORM(GLVersion.GL31, GL31.GL_UNIFORM_BUFFER),
    TEXTURE(GLVersion.GL31, GL31.GL_TEXTURE_BUFFER),
    COPY_READ(GLVersion.GL31, GL31.GL_COPY_READ_BUFFER),
    COPY_WRITE(GLVersion.GL31, GL31.GL_COPY_WRITE_BUFFER),
    DRAW_INDIRECT(GLVersion.GL40, GL40.GL_DRAW_INDIRECT_BUFFER),
    ATOMIC_COUNTER(GLVersion.GL42, GL42.GL_ATOMIC_COUNTER_BUFFER),
    DISPATCH_INDIRECT(GLVersion.GL43, GL43.GL_DISPATCH_INDIRECT_BUFFER),
    SHADER_STORAGE(GLVersion.GL43, GL43.GL_SHADER_STORAGE_BUFFER);

    public final GLVersion version;
    public final int id;

    GLBuffer(GLVersion version, int id) {
        this.version = version;
        this.id = id;
    }

}
