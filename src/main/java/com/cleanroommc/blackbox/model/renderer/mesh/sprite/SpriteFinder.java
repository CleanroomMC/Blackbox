package com.cleanroommc.blackbox.model.renderer.mesh.sprite;

import com.cleanroommc.blackbox.model.renderer.mesh.quad.QuadView;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteFinder {

    TextureAtlasSprite find(QuadView quad, int textureIndex);

    TextureAtlasSprite find(float u, float v);

}
