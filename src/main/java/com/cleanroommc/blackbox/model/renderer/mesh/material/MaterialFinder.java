package com.cleanroommc.blackbox.model.renderer.mesh.material;

public interface MaterialFinder {

    RenderMaterial find();

    MaterialFinder clear();

    MaterialFinder setDepth(int depth);

    MaterialFinder setBlendMode(int spriteIndex, BlendMode blendMode);

    MaterialFinder disableColourIndex(int spriteIndex);

    MaterialFinder disableDiffusedLighting(int spriteIndex);

    MaterialFinder disableAO(int spriteIndex);

    MaterialFinder enableEmissivity(int spriteIndex);

}
