package com.cleanroommc.blackbox.pipeline.emissivity;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class EmissiveTextureAtlasSprite extends TextureAtlasSprite implements IEmissiveTexture {

    private int emissivity = 255;

    public EmissiveTextureAtlasSprite(String spriteName) {
        super(spriteName);
    }

    @Override
    public int getEmissivity() {
        return emissivity;
    }

    @Override
    public void setEmissive(int emissivity) {
        this.emissivity = emissivity;
    }

}
