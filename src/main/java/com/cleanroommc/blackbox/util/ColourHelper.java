package com.cleanroommc.blackbox.util;

public class ColourHelper {

    private static final long MASK1 = 0x00FF00FF;
    private static final long MASK2 = 0xFF00FF00;

    public static int argbToABGR(int color, int alpha) {
        return Integer.reverseBytes(color << 8 | alpha);
    }

    public static int argbToABGR(int color) {
        return Integer.reverseBytes(color << 8);
    }

    /**
     * Mixes two ARGB colors using the given ratios. Use {@link ColourHelper#getStartRatio(float)} and
     * {@link ColourHelper#getEndRatio(float)} to convert a floating-point ratio into an integer ratio.
     *
     * This method takes 64-bit inputs to avoid overflows when mixing the alpha channel. The helper method
     * {@link ColourHelper#mixARGB(int, int, int, int)} can be used with 32-bit inputs.
     *
     * @param c1 The first (starting) color to blend with in ARGB format
     * @param c2 The second (ending) color to blend with in ARGB format
     * @param f1 The ratio of the color {@param c1} as calculated by {@link ColourHelper#getStartRatio(float)}
     * @param f2 The ratio of the color {@param c2} as calculated by {@link ColourHelper#getEndRatio(float)}
     * @return The result of ((c1 * f1) + (c2 * f2) as an ARGB-encoded color
     */
    public static long mixARGB(long c1, long c2, int f1, int f2) {
        return ((((((c1 & MASK1) * f1) + ((c2 & MASK1) * f2)) >> 8) & MASK1) |
                (((((c1 & MASK2) * f1) + ((c2 & MASK2) * f2)) >> 8) & MASK2));
    }

    /**
     * Helper method to convert 32-bit integers to 64-bit integers and back.
     * @see ColourHelper#mixARGB(long, long, int, int)
     */
    public static int mixARGB(int c1, int c2, int f1, int f2) {
        return (int) mixARGB(Integer.toUnsignedLong(c1), Integer.toUnsignedLong(c2), f1, f2);
    }

    public static int getStartRatio(float frac) {
        return (int) (256 * frac);
    }

    public static int getEndRatio(float frac) {
        return 256 - getStartRatio(frac);
    }

    public static class ABGR implements ColourU8 {

        /**
         * Packs the specified color components into ABGR format.
         * @param r The red component of the color
         * @param g The green component of the color
         * @param b The blue component of the color
         * @param a The alpha component of the color
         */
        public static int pack(int r, int g, int b, int a) {
            return (a & 0xFF) << 24 | (b & 0xFF) << 16 | (g & 0xFF) << 8 | (r & 0xFF);
        }

        /**
         * @see ABGR#pack(int, int, int, int)
         */
        public static int pack(float r, float g, float b, float a) {
            return pack((int) (r * COMPONENT_RANGE), (int) (g * COMPONENT_RANGE), (int) (b * COMPONENT_RANGE), (int) (a * COMPONENT_RANGE));
        }

        /**
         * Multiplies the RGB components of the packed ABGR color using the given scale factors.
         * @param color The ABGR packed color to be multiplied
         * @param rw The red component scale factor
         * @param gw The green component scale factor
         * @param bw The blue component scale factor
         */
        public static int mul(int color, float rw, float gw, float bw) {
            float r = unpackRed(color) * rw;
            float g = unpackGreen(color) * gw;
            float b = unpackBlue(color) * bw;

            return pack((int) r, (int) g, (int) b, 0xFF);
        }

        public static int mul(int color, float w) {
            return mul(color, w, w, w);
        }

        /**
         * @param color The packed 32-bit ABGR color to unpack
         * @return The red color component in the range of 0..255
         */
        public static int unpackRed(int color) {
            return color & 0xFF;
        }

        /**
         * @param color The packed 32-bit ABGR color to unpack
         * @return The green color component in the range of 0..255
         */
        public static int unpackGreen(int color) {
            return color >> 8 & 0xFF;
        }

        /**
         * @param color The packed 32-bit ABGR color to unpack
         * @return The blue color component in the range of 0..255
         */
        public static int unpackBlue(int color) {
            return color >> 16 & 0xFF;
        }

        /**
         * @param color The packed 32-bit ABGR color to unpack
         * @return The red color component in the range of 0..255
         */
        public static int unpackAlpha(int color) {
            return color >> 24 & 0xFF;
        }

        public static int pack(float r, float g, float b) {
            return pack(r, g, b, 255);
        }

    }

    public interface ColourU8 {
        /**
         * The maximum value of a color component.
         */
        float COMPONENT_RANGE = 255.0f;

        /**
         * Constant value which can be multiplied with a floating-point color component to get the normalized value. The
         * multiplication is slightly faster than a floating point division, and this code is a hot path which justifies it.
         */
        float NORM = 1.0f / COMPONENT_RANGE;

        /**
         * Normalizes a color component to the range of 0..1.
         */
        static float normalize(float v) {
            return v * NORM;
        }
    }

    private ColourHelper() { }

}
