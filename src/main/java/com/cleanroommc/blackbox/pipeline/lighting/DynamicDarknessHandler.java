package com.cleanroommc.blackbox.pipeline.lighting;

import com.cleanroommc.blackbox.config.category.lighting.DynamicDarknessConfig;
import com.cleanroommc.blackbox.pipeline.core.mixins.EntityRendererAccessor;
import com.cleanroommc.blackbox.util.MathUtil;
import com.cleanroommc.blackbox.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public enum DynamicDarknessHandler {

    INSTANCE;

    private final float[] colourCache = { 0F, 0F, 0F };

    public void updateLightmap(EntityRendererAccessor entityRenderer, Minecraft mc, float partialTicks, int[] colourTable, float bossColorModifier, float bossColorModifierPrev,
                               float torchFlickerX) {
        World world = mc.world;
        boolean hasLightning = world.getLastLightningBolt() > 0;
        boolean isEnd = world.provider.getDimensionType() == DimensionType.THE_END;

        EntityPlayer player = mc.player;
        boolean hasNightVision = mc.player.isPotionActive(MobEffects.NIGHT_VISION);

        boolean needLuminating = DynamicDarknessConfig.enabled && !hasLightning && !hasNightVision;

        float[] brightnessTable = world.provider.getLightBrightnessTable();
        float sunBrightness = world.getSunBrightness(1.0F);
        float moonBrightness = needLuminating ? getMoonBrightness(world, partialTicks) : 0F;

        float f1 = sunBrightness * 0.95F + 0.05F;

        float red, green, blue, lRed, lGreen, lBlue;

        for (int i = 0; i < 256; ++i) {
            int skyIndex = i / 16;
            int blockIndex = i % 16;

            float brightness = hasLightning ? brightnessTable[skyIndex] : brightnessTable[skyIndex] * f1;
            float flicker = torchFlickerX * 0.1F + 1.5F;
            float flickerBrightness = brightnessTable[blockIndex] * flicker;

            float f4 = brightness * (sunBrightness * 0.65F + 0.35F);
            float f6 = flickerBrightness * ((flickerBrightness * 0.6F + 0.4F) * 0.6F + 0.4F);
            float f7 = flickerBrightness * (flickerBrightness * flickerBrightness * 0.6F + 0.4F);

            red = (f4 + flickerBrightness) * 0.96F + 0.03F;
            green = (f4 + f6) * 0.96F + 0.03F;
            blue = (brightness + f7) * 0.96F + 0.03F;

            if (bossColorModifier > 0.0F) {
                float bossColorModifierDelta = bossColorModifierPrev + (bossColorModifier - bossColorModifierPrev) * partialTicks;
                red = red * (1.0F - bossColorModifierDelta) + red * 0.7F * bossColorModifierDelta;
                green = green * (1.0F - bossColorModifierDelta) + green * 0.6F * bossColorModifierDelta;
                blue = blue * (1.0F - bossColorModifierDelta) + blue * 0.6F * bossColorModifierDelta;
            }

            if (isEnd) {
                red = 0.22F + flickerBrightness * 0.75F;
                green = 0.28F + f6 * 0.75F;
                blue = 0.25F + f7 * 0.75F;
            }

            colourCache[0] = red;
            colourCache[1] = green;
            colourCache[2] = blue;
            world.provider.getLightmapColors(partialTicks, sunBrightness, brightness, flickerBrightness, colourCache);
            red = MathHelper.clamp(colourCache[0], 0F, 1F);
            green = MathHelper.clamp(colourCache[1], 0F, 1F);
            blue = MathHelper.clamp(colourCache[2], 0F, 1F);

            if (hasNightVision) {
                float nightVisionBrightness = entityRenderer.invokeGetNightVisionBrightness(player, partialTicks);
                float diff = 1.0F / red;
                if (diff > 1.0F / green) {
                    diff = 1.0F / green;
                }
                if (diff > 1.0F / blue) {
                    diff = 1.0F / blue;
                }
                red = MathHelper.clamp(red * (1.0F - nightVisionBrightness) + red * diff * nightVisionBrightness, 0F, 1F);
                green = MathHelper.clamp(green * (1.0F - nightVisionBrightness) + green * diff * nightVisionBrightness, 0F, 1F);
                blue = MathHelper.clamp(blue * (1.0F - nightVisionBrightness) + blue * diff * nightVisionBrightness, 0F, 1F);
            }

            float gamma = mc.gameSettings.gammaSetting;
            float rDiff = 1.0F - red;
            float gDiff = 1.0F - green;
            float bDiff = 1.0F - blue;
            rDiff = 1.0F - rDiff * rDiff * rDiff * rDiff;
            gDiff = 1.0F - gDiff * gDiff * gDiff * gDiff;
            bDiff = 1.0F - bDiff * bDiff * bDiff * bDiff;
            red = MathHelper.clamp((red * (1.0F - gamma) + rDiff * gamma) * 0.96F + 0.03F, 0F, 1F);
            green = MathHelper.clamp((green * (1.0F - gamma) + gDiff * gamma) * 0.96F + 0.03F, 0F, 1F);
            blue = MathHelper.clamp((blue * (1.0F - gamma) + bDiff * gamma) * 0.96F + 0.03F, 0F, 1F);

            int colourBase = 0xFF000000 | ((int) (red * 255.0F) << 16) | ((int) (green * 255.0F) << 8) | (int) (blue * 255.0F);

            if (needLuminating) {
                float skyFactor = 1F - skyIndex / 15F;
                skyFactor = 1 - skyFactor * skyFactor * skyFactor * skyFactor;
                skyFactor *= moonBrightness;

                float min = skyFactor * 0.05F;
                float rawAmbient = sunBrightness * skyFactor;
                float minAmbient = rawAmbient * (1 - min) + min;
                float skyBase = brightnessTable[skyIndex] * minAmbient;
                min = 0.35F * skyFactor;

                float skyRed = skyBase * (rawAmbient * (1 - min) + min);
                float skyGreen = skyRed;
                float skyBlue = skyBase;

                if (bossColorModifier > 0.0F) {
                    float d = bossColorModifier - bossColorModifierPrev;
                    float m = bossColorModifierPrev + partialTicks * d;
                    skyRed = skyRed * (1.0F - m) + skyRed * 0.7F * m;
                    skyGreen = skyGreen * (1.0F - m) + skyGreen * 0.6F * m;
                    skyBlue = skyBlue * (1.0F - m) + skyBlue * 0.6F * m;
                }

                float blockFactor = 1F;
                if (true) { // check
                    blockFactor = 1F - blockIndex / 15F;
                    blockFactor = 1 - blockFactor * blockFactor * blockFactor * blockFactor;
                }

                float blockBase = blockFactor * flickerBrightness;
                min = 0.4F * blockFactor;
                float blockGreen = blockBase * ((blockBase * (1 - min) + min) * (1 - min) + min);
                float blockBlue = blockBase * (blockBase * blockBase * (1 - min) + min);

                lRed = skyRed + blockBase;
                lGreen = skyGreen + blockGreen;
                lBlue = skyBlue + blockBlue;

                float factor = Math.max(skyFactor, blockFactor);
                min = 0.03F * factor;

                if (isEnd) {
                    lRed = MathHelper.clamp(skyFactor * 0.22F + blockBase * 0.75F, 0F, 1F);
                    lGreen = MathHelper.clamp(skyFactor * 0.28F + blockGreen * 0.75F, 0F, 1F);
                    lBlue = MathHelper.clamp(skyFactor * 0.25F + blockBlue * 0.75F, 0F, 1F);
                } else {
                    lRed = MathHelper.clamp(lRed * (0.99F - min) + min, 0F, 1F);
                    lGreen = MathHelper.clamp(lGreen * (0.99F - min) + min, 0F, 1F);
                    lBlue = MathHelper.clamp(lBlue * (0.99F - min) + min, 0F, 1F);
                }

                float gammaCalculated = gamma * factor;
                float invRed = 1.0F - lRed;
                float invGreen = 1.0F - lGreen;
                float invBlue = 1.0F - lBlue;
                invRed = 1.0F - invRed * invRed * invRed * invRed;
                invGreen = 1.0F - invGreen * invGreen * invGreen * invGreen;
                invBlue = 1.0F - invBlue * invBlue * invBlue * invBlue;
                lRed = lRed * (1.0F - gammaCalculated) + invRed * gammaCalculated;
                lGreen = lGreen * (1.0F - gammaCalculated) + invGreen * gammaCalculated;
                lBlue = lBlue * (1.0F - gammaCalculated) + invBlue * gammaCalculated;
                min = 0.03F * factor;

                lRed = MathHelper.clamp(lRed * (0.99F - min) + min, 0F, 1F);
                lGreen = MathHelper.clamp(lGreen * (0.99F - min) + min, 0F, 1F);
                lBlue = MathHelper.clamp(lBlue * (0.99F - min) + min, 0F, 1F);

                colourTable[i] = RenderHelper.illuminate(red, green, blue, RenderHelper.getLuminanceFromRGB(lRed, lGreen, lBlue));
            } else {
                colourTable[i] = colourBase;
            }
        }
    }

    public float getMoonBrightness(World world, float partialTicks) {
        WorldProvider provider = world.provider;
        if (provider.hasSkyLight()) {
            float celestialAngle = world.getCelestialAngle(partialTicks);
            if (celestialAngle <= 0.25F || celestialAngle >= 0.75F) {
                return 1F;
            }
            float weight = Math.max(0, 20 * (Math.abs(celestialAngle - 0.5F) - 0.2F));
            float interpolation = MathUtil.lerp(DynamicDarknessConfig.ignoreMoonPhases ? 0 : world.getCurrentMoonPhaseFactor(),
                    (float) DynamicDarknessConfig.minMoonBrightness, (float) DynamicDarknessConfig.maxMoonBrightness);
            return MathUtil.lerp(weight * weight * weight, interpolation, 1F);
        }
        return 0F;
    }

}
