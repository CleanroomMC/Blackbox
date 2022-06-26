package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.pipeline.rendering.quad.BakedQuadExtension;
import com.cleanroommc.blackbox.pipeline.rendering.quad.flags.BakedQuadFlags;
import com.cleanroommc.blackbox.util.BakedQuadHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedQuad.class)
public class BakedQuadMixin implements BakedQuadExtension {

    @Shadow @Final protected int[] vertexData;

    @Unique private int cachedFlags = -1;

    @Override
    public float getX(int index) {
        return Float.intBitsToFloat(this.vertexData[BakedQuadHelper.getVertexOffset(index) + BakedQuadHelper.POSITION_X_INDEX]);
    }

    @Override
    public float getY(int index) {
        return Float.intBitsToFloat(this.vertexData[BakedQuadHelper.getVertexOffset(index) + BakedQuadHelper.POSITION_Y_INDEX]);
    }

    @Override
    public float getZ(int index) {
        return Float.intBitsToFloat(this.vertexData[BakedQuadHelper.getVertexOffset(index) + BakedQuadHelper.POSITION_Z_INDEX]);
    }

    @Override
    public int getColour(int index) {
        return this.vertexData[BakedQuadHelper.getVertexOffset(index) + BakedQuadHelper.COLOUR_INDEX];
    }

    @Override
    public float getU(int index) {
        return Float.intBitsToFloat(this.vertexData[BakedQuadHelper.getVertexOffset(index) + BakedQuadHelper.TEXTURE_U_INDEX]);
    }

    @Override
    public float getV(int index) {
        return Float.intBitsToFloat(this.vertexData[BakedQuadHelper.getVertexOffset(index) + BakedQuadHelper.TEXTURE_V_INDEX]);
    }

    @Override
    public int getFlags() {
        if (this.cachedFlags == -1) {
            this.cachedFlags = BakedQuadFlags.getQuadFlags((BakedQuad) (Object) this);
        }
        return this.cachedFlags;
    }

}
