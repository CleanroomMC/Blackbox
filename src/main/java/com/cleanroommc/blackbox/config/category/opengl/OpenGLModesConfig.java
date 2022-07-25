package com.cleanroommc.blackbox.config.category.opengl;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.config.core.RefreshScene;
import com.cleanroommc.blackbox.opengl.buffer.GLBufferGrowRoutine;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

@Config(modid = Blackbox.ID, name = "/blackbox/opengl/modes", category = "modes")
@LangKey("blackbox.opengl.modes")
public class OpenGLModesConfig {

    @RefreshScene
    @LangKey("blackbox.opengl.modes.buffer_growing_mechanism")
    public static GLBufferGrowRoutine bufferGrowingMechanism = GLBufferGrowRoutine.getSuitable();

}
