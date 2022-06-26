package com.cleanroommc.blackbox.pipeline.rendering.quad;

public interface BakedQuadExtension {

    float getX(int index);

    float getY(int index);

    float getZ(int index);

    int getColour(int index);

    int getU(int index);

    int getV(int index);

    int getFlags();

}
