package com.cleanroommc.blackbox.model.renderer.mesh.quad;

import com.cleanroommc.blackbox.model.renderer.mesh.material.RenderMaterial;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec2f;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

public interface MutableQuadView extends QuadView {

    /**
     * Causes texture to appear with no rotation.
     * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
     */
    int BAKE_ROTATE_NONE = 0;

    /**
     * Causes texture to appear rotated 90 deg. clockwise relative to nominal face.
     * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
     */
    int BAKE_ROTATE_90 = 1;

    /**
     * Causes texture to appear rotated 180 deg. relative to nominal face.
     * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
     */
    int BAKE_ROTATE_180 = 2;

    /**
     * Causes texture to appear rotated 270 deg. clockwise relative to nominal face.
     * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
     */
    int BAKE_ROTATE_270 = 3;

    /**
     * When enabled, texture coordinate are assigned based on vertex position.
     * Any existing uv coordinates will be replaced.
     * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
     *
     * <p>UV lock always derives texture coordinates based on nominal face, even
     * when the quad is not co-planar with that face, and the result is
     * the same as if the quad were projected onto the nominal face, which
     * is usually the desired result.
     */
    int BAKE_LOCK_UV = 4;

    /**
     * When set, U texture coordinates for the given sprite are
     * flipped as part of baking. Can be useful for some randomization
     * and texture mapping scenarios. Results are different than what
     * can be obtained via rotation and both can be applied.
     * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
     */
    int BAKE_FLIP_U = 8;

    /**
     * Same as {@link MutableQuadView#BAKE_FLIP_U} but for V coordinate.
     */
    int BAKE_FLIP_V = 16;

    /**
     * UV coordinates by default are assumed to be 0-16 scale for consistency
     * with conventional Minecraft model format. This is scaled to 0-1 during
     * baking before interpolation. Model loaders that already have 0-1 coordinates
     * can avoid wasteful multiplication/division by passing 0-1 coordinates directly.
     * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
     */
    int BAKE_NORMALIZED = 32;

    MutableQuadView setMaterial(RenderMaterial material);

    MutableQuadView setCullFace(@Nullable EnumFacing cullFace);

    MutableQuadView setNominalFace(@Nullable EnumFacing nominalFace);

    MutableQuadView setColourIndex(int colourIndex);

    MutableQuadView fromVanilla(BakedQuad quad, RenderMaterial material, EnumFacing cullFace);

    MutableQuadView setTag(int tag);

    MutableQuadView setPos(int vertexIndex, float x, float y, float z);

    default MutableQuadView setPos(int vertexIndex, Vector3f vec) {
        return setPos(vertexIndex, vec.x, vec.y, vec.z);
    }

    MutableQuadView setNormal(int vertexIndex, float x, float y, float z);

    default MutableQuadView setNormal(int vertexIndex, Vector3f vec) {
        return setNormal(vertexIndex, vec.x, vec.y, vec.z);
    }

    MutableQuadView setLightmap(int vertexIndex, int lightmap);

    default MutableQuadView setLightmap(int a, int b, int c, int d) {
        setLightmap(0, a);
        setLightmap(1, b);
        setLightmap(2, c);
        setLightmap(3, d);
        return this;
    }

    MutableQuadView setSprite(int vertexIndex, int spriteIndex, float u, float v);

    default MutableQuadView setSprite(int vertexIndex, int spriteIndex, Vec2f vec) {
        return setSprite(vertexIndex, spriteIndex, vec.x, vec.y);
    }

    MutableQuadView setSpriteColour(int vertexIndex, int spriteIndex, int colour);

    default MutableQuadView setSpriteColour(int spriteIndex, int a, int b, int c, int d) {
        setSpriteColour(0, spriteIndex, a);
        setSpriteColour(1, spriteIndex, b);
        setSpriteColour(2, spriteIndex, c);
        setSpriteColour(3, spriteIndex, d);
        return this;
    }

    MutableQuadView spriteBake(int spriteIndex, TextureAtlasSprite sprite, int bakeFlags);

}
