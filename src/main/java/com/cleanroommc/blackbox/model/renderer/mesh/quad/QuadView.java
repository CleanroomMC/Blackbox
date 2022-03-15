package com.cleanroommc.blackbox.model.renderer.mesh.quad;

import com.cleanroommc.blackbox.model.renderer.mesh.material.RenderMaterial;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

public interface QuadView {

    void toVanilla(int spriteIndex, int[] target, int targetIndex, VertexFormat vertexFormat);

    default BakedQuad toVanilla(int spriteIndex, TextureAtlasSprite sprite, VertexFormat vertexFormat) {
        int[] vertexData = new int[28];
        toVanilla(spriteIndex, vertexData, 0, vertexFormat);
        return new BakedQuad(vertexData, getColourIndex(), getFace(), sprite, true, vertexFormat);
    }

    void copyTo(MutableQuadView target);

    RenderMaterial getMaterial();

    int getColourIndex();

    EnumFacing getFace();

    @Nullable EnumFacing getCullFace();

    @Nullable EnumFacing getNominalFace();

    int getTag();

    Vector3f copyPos(int vertexIndex, @Nullable Vector3f target);

    float findPosByIndex(int vertexIndex, int coordinateIndex);

    float findX(int vertexIndex);

    float findY(int vertexIndex);

    float findZ(int vertexIndex);

    boolean hasNormal(int vertexIndex);

    @Nullable Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target);

    float findNormalX(int vertexIndex);

    float findNormalY(int vertexIndex);

    float findNormalZ(int vertexIndex);

    int getLightmap(int vertexIndex);

    int getSpriteColour(int vertexIndex, int spriteIndex);

    int getSpriteU(int vertexIndex, int spriteIndex);

    int getSpriteV(int vertexIndex, int spriteIndex);

}
