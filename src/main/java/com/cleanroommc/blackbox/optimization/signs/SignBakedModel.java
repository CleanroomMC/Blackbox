package com.cleanroommc.blackbox.optimization.signs;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public class SignBakedModel implements IBakedModel {

    private final boolean standing;
    private final TextureAtlasSprite signSprite, postSprite, particleSprite;
    private final EnumMap<EnumFacing, List<BakedQuad>> quads = new EnumMap<>(EnumFacing.class);

    public SignBakedModel(boolean standing, String signSprite, String postSprite, String particleSprite) {
        this.standing = standing;
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        this.signSprite = map.getAtlasSprite(signSprite);
        this.postSprite = map.getAtlasSprite(postSprite);
        this.particleSprite = map.getAtlasSprite(particleSprite);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null || side == null) {
            return Collections.emptyList();
        }
        return quads.computeIfAbsent(side, k -> getQuads(state, side));
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.particleSprite;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    private List<BakedQuad> getQuads(IBlockState state, EnumFacing facing) {
        List<BakedQuad> quads = new ArrayList<>();
        quads.add(Blackbox.FACE_BAKERY.makeBakedQuad(
                new Vector3f(0, 9.33333F, 7.33333F),
                new Vector3f(16, 17.33333F, 8.66663F),
                new BlockPartFace(null, -1, this.signSprite.toString(), new BlockFaceUV(getSignUV(facing), 0)),
                this.signSprite,
                facing,
                ModelRotation.X0_Y0,
                null,
                true,
                true
        ));
        if (this.standing) {
            quads.add(Blackbox.FACE_BAKERY.makeBakedQuad(
                    new Vector3f(7.33333F, 0, 7.33333F),
                    new Vector3f(8.66663F, 9.33333F, 8.66663F),
                    new BlockPartFace(null, -1, this.postSprite.toString(), new BlockFaceUV(getSignPostUV(facing), 0)),
                    this.postSprite,
                    facing,
                    ModelRotation.X0_Y0,
                    null,
                    true,
                    true
            ));
        }
        return quads;
    }

    private float[] getSignUV(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return new float[] { 1, 1, 13, 7 };
            case EAST:
                return new float[] { 0, 1, 1, 7 };
            case SOUTH:
                return new float[] { 1, 9, 13, 15 };
            case WEST:
                return new float[] { 0, 9, 1, 15 };
            case UP:
                return new float[] { 13, 1, 1, 0 };
            case DOWN:
                return new float[] { 12, 8, 0, 9 };
        }
        throw new RuntimeException(". . .");
    }

    private float[] getSignPostUV(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return new float[] { 2, 0, 4, 15 };
            case EAST:
                return new float[] { 0, 0, 2, 15 };
            case SOUTH:
                return new float[] { 6, 0, 8, 15 };
            case WEST:
                return new float[] { 4, 0, 6, 15 };
            case UP:
            case DOWN:
                return new float[] { 0, 0, 2, 2 };
        }
        throw new RuntimeException(". . .");
    }

}
