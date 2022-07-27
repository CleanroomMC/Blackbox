package com.cleanroommc.blackbox.config.category.misc;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

@Config(modid = Blackbox.ID, name = "/blackbox/misc/tab_overlay", category = "tab_overlay")
@LangKey("blackbox.misc.tab_overlay")
public class TabOverlayConfig {

    @LangKey("blackbox.misc.tab_overlay.show_numerical_ping")
    public static boolean showNumericalPing = true;

}
