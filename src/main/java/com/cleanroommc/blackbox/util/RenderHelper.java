package com.cleanroommc.blackbox.util;

public class RenderHelper {

    public static float getLuminanceFromRGB(float r, float g, float b) {
        return r * 0.2126F + g * 0.7152F + b * 0.0722F;
    }

    public static int illuminate(int colour, float luminance) {
        float r = (colour & 0xFF) / 255F;
        float g = ((colour >> 8) & 0xFF) / 255F;
        float b = ((colour >> 16) & 0xFF) / 255F;
        float l = getLuminanceFromRGB(r, g, b);
        if (l <= 0F || luminance >= l) {
            return colour;
        }
        float f = luminance / l;
        colour = 0xFF000000;
        colour |= Math.round(f * r * 255);
        colour |= Math.round(f * g * 255) << 8;
        colour |= Math.round(f * b * 255) << 16;
        return colour;
    }

    public static int illuminate(float r, float g, float b, float luminance) {
        float l = getLuminanceFromRGB(r, g, b);
        if (l <= 0F || luminance >= l) {
            return 0xFF000000 | Math.round(r * 255) | (Math.round(g * 255) << 8) | (Math.round(b * 255) << 16);
        }
        float f = luminance / l;
        return 0xFF000000 | Math.round(f * r * 255) | (Math.round(f * g * 255) << 8) | (Math.round(f * b * 255) << 16);
    }

    private RenderHelper() { }

}
