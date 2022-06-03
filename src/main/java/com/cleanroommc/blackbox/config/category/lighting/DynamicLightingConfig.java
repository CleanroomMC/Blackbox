package com.cleanroommc.blackbox.config.category.lighting;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.SlidingOption;

@Config(modid = Blackbox.ID, name = "/blackbox/lighting/dynamic_lighting")
public class DynamicLightingConfig {

    public static boolean enabled = true;

    @SlidingOption
    @RangeInt(min = 0, max = 1000)
    public static int updateFrequency = 100;

}