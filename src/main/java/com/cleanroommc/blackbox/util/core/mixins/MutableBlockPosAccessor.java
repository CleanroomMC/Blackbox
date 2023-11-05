package com.cleanroommc.blackbox.util.core.mixins;

import net.minecraft.util.math.BlockPos.MutableBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MutableBlockPos.class)
public interface MutableBlockPosAccessor {

    @Accessor(value = "x")
    void setX(int x);

    @Accessor(value = "z")
    void setZ(int Z);

}
