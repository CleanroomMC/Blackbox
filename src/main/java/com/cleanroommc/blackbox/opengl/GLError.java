package com.cleanroommc.blackbox.opengl;

import com.cleanroommc.blackbox.Blackbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nullable;

public enum GLError {

    NO_ERROR(GL11.GL_NO_ERROR),
    INVALID_ENUM(GL11.GL_INVALID_ENUM),
    INVALID_VALUE(GL11.GL_INVALID_VALUE),
    INVALID_OPERATION(GL11.GL_INVALID_OPERATION),
    OUT_OF_MEMORY(GL11.GL_OUT_OF_MEMORY),
    STACK_UNDERFLOW(GL11.GL_STACK_UNDERFLOW),
    STACK_OVERFLOW(GL11.GL_STACK_OVERFLOW),
    INVALID_FRAMEBUFFER_OPERATION(GL30.GL_INVALID_FRAMEBUFFER_OPERATION);

    public static GLError poll() {
        int error = GL11.glGetError();
        switch (error) {
            case GL11.GL_NO_ERROR:
                return NO_ERROR;
            case GL11.GL_INVALID_ENUM:
                return INVALID_ENUM;
            case GL11.GL_INVALID_VALUE:
                return INVALID_VALUE;
            case GL11.GL_INVALID_OPERATION:
                return INVALID_OPERATION;
            case GL11.GL_OUT_OF_MEMORY:
                return OUT_OF_MEMORY;
            case GL11.GL_STACK_UNDERFLOW:
                return STACK_UNDERFLOW;
            case GL11.GL_STACK_OVERFLOW:
                return STACK_OVERFLOW;
            case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
                return INVALID_FRAMEBUFFER_OPERATION;
        }
        throw new UnsupportedOperationException("Unable to retrieve proper GLError from code: " + error);
    }

    public static void print(@Nullable String message) {
        GLError error = poll();
        if (error != NO_ERROR) {
            Blackbox.LOGGER.error("###### Blackbox GLError ######");
            if (message != null) {
                Blackbox.LOGGER.error("@ {}", message);
            }
            Blackbox.LOGGER.error("{} ({})", error, error.id);
        }
    }

    private final int id;

    GLError(int id) {
        this.id = id;
    }

}
