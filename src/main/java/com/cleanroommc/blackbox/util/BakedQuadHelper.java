package com.cleanroommc.blackbox.util;

public class BakedQuadHelper {

    public static final int POSITION_X_INDEX = 0;
    public static final int POSITION_Y_INDEX = 1;
    public static final int POSITION_Z_INDEX = 2;
    public static final int COLOUR_INDEX = 3;
    public static final int TEXTURE_U_INDEX = 4;
    public static final int TEXTURE_V_INDEX = 4;
    public static final int LIGHT_INDEX = 6;

    // Size of vertex format in 4-byte integers
    public static final int VERTEX_SIZE = 8;

    /**
     * @param vertexIndex The index of the vertex to access
     * @return The starting offset of the vertex's attributes
     */
    public static int getVertexOffset(int vertexIndex) {
        return vertexIndex * VERTEX_SIZE;
    }

    private BakedQuadHelper() { }

}
