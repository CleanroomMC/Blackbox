package com.cleanroommc.blackbox.util;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.glu.GLU;

public class GLHelper {

    public static void checkGLError(String message) {
        int i = GlStateManager.glGetError();
        if (i != 0) {
            Blackbox.LOGGER.error("###### Blackbox GL Error ######");
            Blackbox.LOGGER.error("@ {}", message);
            Blackbox.LOGGER.error("{}: {}", i, GLU.gluErrorString(i));
        }
    }

    private GLHelper() { }

}
