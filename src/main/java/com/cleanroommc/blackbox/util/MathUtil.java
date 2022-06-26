package com.cleanroommc.blackbox.util;

import net.minecraft.util.EnumFacing;

public class MathUtil {

    public static final EnumFacing[] FACINGS = EnumFacing.values(); // Make sure it never mutates
    public static final int FACINGS_LENGTH = FACINGS.length;

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

}
