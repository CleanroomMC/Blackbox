package com.cleanroommc.blackbox.pipeline.rendering.quad;

public interface BakedQuadExtension {

    float getX(int index);

    float getY(int index);

    float getZ(int index);

    int getColour(int index);

    float getU(int index);

    float getV(int index);

    int getFlags();

}
