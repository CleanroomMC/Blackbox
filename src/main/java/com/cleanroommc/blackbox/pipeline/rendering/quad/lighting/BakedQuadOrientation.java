package com.cleanroommc.blackbox.pipeline.rendering.quad.lighting;

public enum BakedQuadOrientation {

    NORMAL(new int[] { 0, 1, 2, 3 }),
    FLIP(new int[] { 1, 2, 3, 0 });

    /**
     * Determines the orientation of the vertices in the quad.
     */
    public static BakedQuadOrientation orientByBrightness(float[] brightnesses) {
        // If one side of the quad is brighter, flip the sides
        if (brightnesses[0] + brightnesses[2] > brightnesses[1] + brightnesses[3]) {
            return NORMAL;
        } else {
            return FLIP;
        }
    }

    private final int[] indices;

    BakedQuadOrientation(int[] indices) {
        this.indices = indices;
    }

    /**
     * @return The re-oriented index of the vertex {@param index}
     */
    public int getVertexIndex(int index) {
        return this.indices[index];
    }

}
