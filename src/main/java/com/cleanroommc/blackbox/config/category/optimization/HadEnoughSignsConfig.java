package com.cleanroommc.blackbox.config.category.optimization;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.config.core.RefreshScene;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

@Config(modid = Blackbox.ID, name = "/blackbox/optimization/had_enough_signs", category = "had_enough_signs")
@LangKey("blackbox.optimization.had_enough_signs")
public class HadEnoughSignsConfig {

    @RefreshScene
    @LangKey("blackbox.optimization.had_enough_signs.enabled")
    public static boolean enabled = true;

}
