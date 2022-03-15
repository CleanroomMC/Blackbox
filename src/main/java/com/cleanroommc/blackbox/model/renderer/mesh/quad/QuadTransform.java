package com.cleanroommc.blackbox.model.renderer.mesh.quad;

@FunctionalInterface
public interface QuadTransform {

    boolean transform(MutableQuadView quad);

}
