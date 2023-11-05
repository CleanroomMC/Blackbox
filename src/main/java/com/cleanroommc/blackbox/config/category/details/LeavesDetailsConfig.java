package com.cleanroommc.blackbox.config.category.details;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.config.core.RefreshScene;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail.Natural;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail.Placed;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

@Config(modid = Blackbox.ID, name = "/blackbox/details/leaves", category = "leaves")
@LangKey("blackbox.details.leaves")
public class LeavesDetailsConfig {

    @RefreshScene
    @LangKey("blackbox.details.leaves.leaves_mode")
    public static LeavesDetail leavesMode = LeavesDetail.SMART;

    @RefreshScene
    @LangKey("blackbox.details.leaves.natural_leaves_mode")
    public static LeavesDetail.Natural naturalLeavesMode = Natural.SMART;

    @RefreshScene
    @LangKey("blackbox.details.leaves.placed_eaves_mode")
    public static LeavesDetail.Placed placedLeavesMode = Placed.SMART;


}
