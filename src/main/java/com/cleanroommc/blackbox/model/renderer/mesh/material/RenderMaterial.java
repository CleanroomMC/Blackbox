package com.cleanroommc.blackbox.model.renderer.mesh.material;

import com.cleanroommc.blackbox.Blackbox;
import com.google.common.annotations.Beta;
import net.minecraft.util.ResourceLocation;

public interface RenderMaterial {

    ResourceLocation STANDARD = new ResourceLocation(Blackbox.ID, "standard");

    @Beta int getDepth();

}
