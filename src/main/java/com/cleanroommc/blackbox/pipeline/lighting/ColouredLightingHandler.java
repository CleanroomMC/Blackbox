package com.cleanroommc.blackbox.pipeline.lighting;

import net.minecraft.client.renderer.GLAllocation;

import java.nio.FloatBuffer;

public enum ColouredLightingHandler {

    INSTANCE;

    private static final int LIGHT_ENTRIES = (int) Math.pow(2, 11);
    private static final int STRUCT_SIZE = (4 + 3 + 1); // vec4 (colour) + vec3 (position) + float (radius)

    private FloatBuffer buffer;

    private void createUBO() {
        if (this.buffer == null) {
            this.buffer = GLAllocation.createDirectFloatBuffer(LIGHT_ENTRIES * STRUCT_SIZE);
        }
    }

}
