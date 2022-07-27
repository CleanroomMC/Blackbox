package com.cleanroommc.blackbox.opengl;

import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLSync;

public class GLFenceSync {

    private GLSync fence;

    public void post() {
        clear();
        this.fence = GL32.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    public void sync() {
        if (this.fence != null) {
            int waitReturn = GL32.GL_UNSIGNALED;
            while (waitReturn != GL32.GL_ALREADY_SIGNALED && waitReturn != GL32.GL_CONDITION_SATISFIED) {
                waitReturn = GL32.glClientWaitSync(fence, GL32.GL_SYNC_FLUSH_COMMANDS_BIT, 1);
            }
            GL32.glDeleteSync(this.fence);
        }
        this.fence = null;
    }

    public void clear() {
        if (this.fence != null) {
            GL32.glDeleteSync(this.fence);
            this.fence = null;
        }
    }

}
