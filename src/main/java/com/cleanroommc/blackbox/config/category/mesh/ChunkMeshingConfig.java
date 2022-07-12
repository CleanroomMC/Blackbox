package com.cleanroommc.blackbox.config.category.mesh;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.config.core.RefreshScene;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.SlidingOption;

@Config(modid = Blackbox.ID, name = "/blackbox/mesh/chunk_meshing", category = "chunk_meshing")
@LangKey("blackbox.mesh.chunk_meshing")
public class ChunkMeshingConfig {

    @RefreshScene
    @SlidingOption
    @RangeInt(min = 1, max = 10)
    @LangKey("blackbox.mesh.chunk_meshing.thread_count")
    public static int threadCount = MathHelper.clamp(Math.max(Runtime.getRuntime().availableProcessors() / 3, Runtime.getRuntime().availableProcessors() - 6), 1, 10);

    @RefreshScene
    @SlidingOption
    @RangeInt(min = 1, max = 10)
    @LangKey("blackbox.mesh.chunk_meshing.thread_priority")
    public static int threadPriority = Thread.NORM_PRIORITY - 2;

}
