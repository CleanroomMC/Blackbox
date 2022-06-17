package com.cleanroommc.blackbox.config.category.optimization;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.config.core.RefreshScene;
import net.minecraftforge.common.config.Config;

@Config(modid = Blackbox.ID, name = "/blackbox/optimization/had_enough_signs")
public class HadEnoughSignsConfig {

    @RefreshScene
    public static boolean enabled = true;

}
