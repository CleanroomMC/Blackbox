package com.cleanroommc.blackbox.config.category.lighting;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.RangeDouble;

@Config(modid = Blackbox.ID, name = "/blackbox/lighting/dynamic_darkness")
public class DynamicDarknessConfig {

    public static boolean enabled = true;

    public static boolean ignoreMoonPhases = false;

    @RangeDouble(min = 0.0F, max = 1.0F)
    public static double minMoonBrightness = 0.1F;

    @RangeDouble(min = 0.0F, max = 1.0F)
    public static double maxMoonBrightness = 0.75F;

}
