package com.cleanroommc.blackbox.config.category.fixes;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;

@Config(modid = Blackbox.ID, name = "/blackbox/fixes/falling_block", category = "falling_block")
public class FallingBlockConfig {

    public static boolean fixMC114286 = true;
    public static boolean fixTileEntityRendering = true;

}
