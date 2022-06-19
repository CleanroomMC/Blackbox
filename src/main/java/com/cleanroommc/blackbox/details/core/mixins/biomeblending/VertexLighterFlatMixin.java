package com.cleanroommc.blackbox.details.core.mixins.biomeblending;

import com.cleanroommc.blackbox.details.core.biomeblending.ColourBlender;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.vecmath.Vector3f;

// TODO: removal when pipeline is instated
@Deprecated
@Mixin(VertexLighterFlat.class)
public abstract class VertexLighterFlatMixin extends QuadGatheringTransformer {

    @Shadow @Final protected BlockInfo blockInfo;

    @Shadow protected int posIndex;
    @Shadow protected int lightmapIndex;
    @Shadow protected int colorIndex;
    @Shadow protected int normalIndex;
    @Shadow private int tint;
    @Shadow private boolean diffuse;

    @Shadow protected abstract void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z);
    @Shadow protected abstract void updateColor(float[] normal, float[] color, float x, float y, float z, float tint, int multiplier);
    @Shadow protected abstract void applyAnaglyph(float[] color);

    @Override
    protected void processQuad() {
        float[][] position = quadData[posIndex];
        float[][] normal;
        float[][] lightmap = quadData[lightmapIndex];
        float[][] color = quadData[colorIndex];
        if (dataLength[normalIndex] >= 3 && (quadData[normalIndex][0][0] != -1 ||  quadData[normalIndex][0][1] != -1 ||  quadData[normalIndex][0][2] != -1)) {
            normal = quadData[normalIndex]; // normals must be generated
        } else {
            normal = new float[4][4];
            Vector3f v1 = new Vector3f(position[3]);
            Vector3f t = new Vector3f(position[1]);
            Vector3f v2 = new Vector3f(position[2]);
            v1.sub(t);
            t.set(position[0]);
            v2.sub(t);
            v1.cross(v2, v1);
            v1.normalize();
            for (int v = 0; v < 4; v++) {
                normal[v][0] = v1.x;
                normal[v][1] = v1.y;
                normal[v][2] = v1.z;
                normal[v][3] = 0;
            }
        }
        int[] colours = { -1, -1, -1, -1 };
        if (tint != -1) {
            colours = ColourBlender.getColours(tint, blockInfo);
        }

        VertexFormat format = parent.getVertexFormat();
        int count = format.getElementCount();
        for (int v = 0; v < 4; v++) {
            position[v][0] += blockInfo.getShx();
            position[v][1] += blockInfo.getShy();
            position[v][2] += blockInfo.getShz();

            float x = position[v][0] - .5f;
            float y = position[v][1] - .5f;
            float z = position[v][2] - .5f;

            //if(blockInfo.getBlock().isFullCube())
            //{
                x += normal[v][0] * .5f;
                y += normal[v][1] * .5f;
                z += normal[v][2] * .5f;
            //}

            float blockLight = lightmap[v][0], skyLight = lightmap[v][1];
            updateLightmap(normal[v], lightmap[v], x, y, z);
            if (dataLength[lightmapIndex] > 1) {
                if(blockLight > lightmap[v][0]) lightmap[v][0] = blockLight;
                if(skyLight > lightmap[v][1]) lightmap[v][1] = skyLight;
            }
            updateColor(normal[v], color[v], x, y, z, tint, colours[v]);
            if (diffuse) {
                float d = LightUtil.diffuseLight(normal[v][0], normal[v][1], normal[v][2]);
                for (int i = 0; i < 3; i++) {
                    color[v][i] *= d;
                }
            }
            if (EntityRenderer.anaglyphEnable) {
                applyAnaglyph(color[v]);
            }
            // no need for remapping cause all we could've done is add 1 element to the end
            for (int e = 0; e < count; e++) {
                VertexFormatElement element = format.getElement(e);
                switch (element.getUsage()) {
                    case POSITION:
                        // position adding moved to VertexBufferConsumer due to x and z not fitting completely into a float
                        /*float[] pos = new float[4];
                        System.arraycopy(position[v], 0, pos, 0, position[v].length);
                        pos[0] += blockInfo.getBlockPos().getX();
                        pos[1] += blockInfo.getBlockPos().getY();
                        pos[2] += blockInfo.getBlockPos().getZ();*/
                        parent.put(e, position[v]);
                        break;
                    case NORMAL:
                        parent.put(e, normal[v]);
                        break;
                    case COLOR:
                        parent.put(e, color[v]);
                        break;
                    case UV:
                        if (element.getIndex() == 1) {
                            parent.put(e, lightmap[v]);
                            break;
                        }
                        // else fallthrough to default
                    default:
                        parent.put(e, quadData[e][v]);
                }
            }
        }
        tint = -1;
    }

}
