package com.cleanroommc.blackbox.model.renderer.mesh.quad;

import com.cleanroommc.blackbox.model.renderer.mesh.material.RenderMaterial;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec2f;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

public interface QuadEmitter extends MutableQuadView {

    float CULL_FACE_EPSILON = 0.00001f;

    @Override
    QuadEmitter setMaterial(RenderMaterial material);

    @Override
    QuadEmitter setCullFace(@Nullable EnumFacing cullFace);

    @Override
    QuadEmitter setNominalFace(@Nullable EnumFacing nominalFace);

    @Override
    QuadEmitter setColourIndex(int colourIndex);

    @Override
    QuadEmitter fromVanilla(BakedQuad quad, RenderMaterial material, EnumFacing cullFace);

    @Override
    QuadEmitter setTag(int tag);

    @Override
    QuadEmitter setPos(int vertexIndex, float x, float y, float z);

    @Override
    default QuadEmitter setPos(int vertexIndex, Vector3f vec) {
        MutableQuadView.super.setPos(vertexIndex, vec);
        return this;
    }

    @Override
    QuadEmitter setNormal(int vertexIndex, float x, float y, float z);

    @Override
    default QuadEmitter setNormal(int vertexIndex, Vector3f vec) {
        MutableQuadView.super.setNormal(vertexIndex, vec);
        return this;
    }

    @Override
    QuadEmitter setLightmap(int vertexIndex, int lightmap);

    @Override
    default QuadEmitter setLightmap(int a, int b, int c, int d) {
        MutableQuadView.super.setLightmap(a, b, c, d);
        return this;
    }

    @Override
    QuadEmitter setSprite(int vertexIndex, int spriteIndex, float u, float v);

    @Override
    default QuadEmitter setSprite(int vertexIndex, int spriteIndex, Vec2f vec) {
        MutableQuadView.super.setSprite(vertexIndex, spriteIndex, vec);
        return this;
    }

    default QuadEmitter setSpriteInUnitSquare(int spriteIndex) {
        setSprite(0, spriteIndex, 0, 0);
        setSprite(1, spriteIndex, 0, 1);
        setSprite(2, spriteIndex, 1, 1);
        setSprite(3, spriteIndex, 1, 0);
        return this;
    }

    @Override
    QuadEmitter setSpriteColour(int vertexIndex, int spriteIndex, int colour);

    @Override
    default QuadEmitter setSpriteColour(int spriteIndex, int a, int b, int c, int d) {
        MutableQuadView.super.setSpriteColour(spriteIndex, a, b, c, d);
        return this;
    }

    @Override
    QuadEmitter spriteBake(int spriteIndex, TextureAtlasSprite sprite, int bakeFlags);

    QuadEmitter emit();

    default QuadEmitter square(EnumFacing nominalFace, float left, float bottom, float right, float top, float depth) {
        if (Math.abs(depth) < CULL_FACE_EPSILON) {
            setCullFace(nominalFace);
            depth = 0;
        } else {
            setCullFace(null);
        }
        setNominalFace(nominalFace);
        switch (nominalFace) {
            case UP:
                depth = 1 - depth;
                top = 1 - top;
                bottom = 1 - bottom;
            case DOWN:
                setPos(0, left, depth, top);
                setPos(1, left, depth, bottom);
                setPos(2, right, depth, bottom);
                setPos(3, right, depth, top);
                break;
            case EAST:
                depth = 1 - depth;
                left = 1 - left;
                right = 1 - right;
            case WEST:
                setPos(0, depth, top, left);
                setPos(1, depth, bottom, left);
                setPos(2, depth, bottom, right);
                setPos(3, depth, top, right);
                break;
            case SOUTH:
                depth = 1 - depth;
                left = 1 - left;
                right = 1 - right;
            case NORTH:
                setPos(0, 1 - left, top, depth);
                setPos(1, 1 - left, bottom, depth);
                setPos(2, 1 - right, bottom, depth);
                setPos(3, 1 - right, top, depth);
                break;
        }
        return this;
    }

}
