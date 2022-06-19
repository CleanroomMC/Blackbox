package com.cleanroommc.blackbox.config.category.details;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.config.core.RefreshScene;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail.Natural;
import com.cleanroommc.blackbox.details.core.leaves.LeavesDetail.Placed;
import net.minecraftforge.common.config.Config;

@Config(modid = Blackbox.ID, name = "/blackbox/details/leaves", category = "leaves")
public class LeavesDetailsConfig {

    @RefreshScene
    public static LeavesDetail leavesMode = LeavesDetail.SMART;

    @RefreshScene
    public static LeavesDetail.Natural naturalLeavesMode = Natural.SMART;

    @RefreshScene
    public static LeavesDetail.Placed placedLeavesMode = Placed.SMART;


}
