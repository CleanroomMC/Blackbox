package com.cleanroommc.blackbox.config.category.lighting;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.SlidingOption;

@Config(modid = Blackbox.ID, name = "/blackbox/lighting/dynamic_darkness", category = "dynamic_darkness")
@LangKey("blackbox.lighting.dynamic_darkness")
public class DynamicDarknessConfig {

    @LangKey("blackbox.lighting.dynamic_darkness.enabled")
    public static boolean enabled = true;

    @LangKey("blackbox.lighting.dynamic_darkness.ignore_moon_phases")
    public static boolean ignoreMoonPhases = false;

    @SlidingOption
    @RangeDouble(min = 0.0F, max = 1.0F)
    @LangKey("blackbox.lighting.dynamic_darkness.min_moon_brightness")
    public static double minMoonBrightness = 0.05D;

    @SlidingOption
    @RangeDouble(min = 0.0F, max = 1.0F)
    @LangKey("blackbox.lighting.dynamic_darkness.max_moon_brightness")
    public static double maxMoonBrightness = 0.75D;

}
