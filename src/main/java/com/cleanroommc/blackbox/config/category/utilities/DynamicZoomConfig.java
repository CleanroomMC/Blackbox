package com.cleanroommc.blackbox.config.category.utilities;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.SlidingOption;

@Config(modid = Blackbox.ID, name = "/blackbox/utilities/dynamic_zoom", category = "dynamic_zoom")
@LangKey("blackbox.utilities.dynamic_zoom")
public class DynamicZoomConfig {

    @LangKey("blackbox.utilities.dynamic_zoom.save_zoom_level")
    public static boolean saveZoomLevel = false;

    @SlidingOption
    @RangeInt(min = 2, max = 100)
    @LangKey("blackbox.utilities.dynamic_zoom.default_zoom_level")
    public static int defaultZoomLevel = 2;

    @SlidingOption
    @RangeInt(min = 2, max = 100)
    @LangKey("blackbox.utilities.dynamic_zoom.max_zoom_level")
    public static int maxZoomLevel = 100;

    @SlidingOption
    @RangeDouble(min = 1.001D, max = 2.000D)
    @LangKey("blackbox.utilities.dynamic_zoom.zoom_stepping")
    public static double zoomStepping = 1.101D;

}
