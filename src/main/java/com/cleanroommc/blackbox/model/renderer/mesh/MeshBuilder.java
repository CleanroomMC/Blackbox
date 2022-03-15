package com.cleanroommc.blackbox.model.renderer.mesh;

import com.cleanroommc.blackbox.model.renderer.mesh.quad.QuadEmitter;

public interface MeshBuilder {

   QuadEmitter getEmitter();

   Mesh build();

}
