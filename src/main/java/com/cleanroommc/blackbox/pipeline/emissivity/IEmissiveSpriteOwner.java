package com.cleanroommc.blackbox.pipeline.emissivity;

public interface IEmissiveSpriteOwner {

    boolean hasEmissiveVersion();

    EmissiveTextureAtlasSprite getEmissiveVersion();

    void setEmissiveVersion(EmissiveTextureAtlasSprite emissiveVersion);

}
