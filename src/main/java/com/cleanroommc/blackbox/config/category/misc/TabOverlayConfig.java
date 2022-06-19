package com.cleanroommc.blackbox.config.category.misc;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;

@Config(modid = Blackbox.ID, name = "/blackbox/misc/tab_overlay", category = "tab_overlay")
public class TabOverlayConfig {

    public static boolean showNumericalPing = true;

}
