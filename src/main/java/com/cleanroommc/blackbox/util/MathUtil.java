package com.cleanroommc.blackbox.util;

public class MathUtil {

    public static float simpleLerp(float a, float b, float f) {
        return a + b * (f - b);
    }

}
