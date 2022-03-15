package com.cleanroommc.blackbox.model.renderer.mesh;

import com.cleanroommc.blackbox.model.renderer.mesh.quad.QuadView;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public interface Mesh extends Iterable<QuadView> {

    List<BakedQuad> encode();

}
