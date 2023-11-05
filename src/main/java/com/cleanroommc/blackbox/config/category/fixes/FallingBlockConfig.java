package com.cleanroommc.blackbox.config.category.fixes;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

@Config(modid = Blackbox.ID, name = "/blackbox/fixes/falling_block", category = "falling_block")
@LangKey("blackbox.fixes.falling_block")
public class FallingBlockConfig {

    @LangKey("blackbox.fixes.falling_block.fixmc114286")
    public static boolean fixMC114286 = true;

    @LangKey("blackbox.fixes.falling_block.fix_tile_entity_rendering")
    public static boolean fixTileEntityRendering = true;

}
