package com.cleanroommc.blackbox.fixes.core.mixins.fallingblock;

import com.cleanroommc.blackbox.config.category.fixes.FallingBlockConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RenderFallingBlock.class)
public abstract class RenderFallingBlockMixin extends Render<EntityFallingBlock> {

    @Unique private static final MutableBlockPos mutablePos = new MutableBlockPos();

    @Unique private IBlockState previousBlockState;
    @Unique private boolean checkedForTile;
    @Unique private TileEntity cachedTileEntity;

    protected RenderFallingBlockMixin(RenderManager renderManager) {
        super(renderManager);
        throw new AssertionError();
    }

    /**
     * @author Rongmario
     * @reason Fixes things listed in {@link FallingBlockConfig}
     */
    @Overwrite
    public void doRender(EntityFallingBlock entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.getBlock() != null) {
            IBlockState state = entity.getBlock();
            if (state.getRenderType() == EnumBlockRenderType.INVISIBLE || state.getRenderType() == EnumBlockRenderType.LIQUID) {
                return;
            }
            if (state.getRenderType() == EnumBlockRenderType.ENTITYBLOCK_ANIMATED) {
                if (previousBlockState != state) {
                    this.checkedForTile = false;
                    this.cachedTileEntity = null;
                }
                if (!FallingBlockConfig.fixTileEntityRendering || (this.checkedForTile && this.cachedTileEntity == null)) {
                    return;
                }
                if (this.cachedTileEntity == null) {
                    this.checkedForTile = true;
                    this.cachedTileEntity = state.getBlock().createTileEntity(entity.world, state);
                    if (this.cachedTileEntity == null) {
                        return;
                    }
                    if (this.cachedTileEntity.hasFastRenderer()) {
                        this.cachedTileEntity = null; // TODO: support FastTESRs
                        return;
                    }
                    this.previousBlockState = state;
                }
                this.cachedTileEntity.setWorld(entity.world);
                this.cachedTileEntity.setPos(mutablePos.setPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ));
                TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
                if (this.cachedTileEntity.getDistanceSq(dispatcher.entityX, dispatcher.entityY, dispatcher.entityZ) < this.cachedTileEntity.getMaxRenderDistanceSquared()) {
                    RenderHelper.enableStandardItemLighting();
                    dispatcher.render(this.cachedTileEntity,
                            entity.posX - TileEntityRendererDispatcher.staticPlayerX,
                            entity.posY - TileEntityRendererDispatcher.staticPlayerY,
                            entity.posZ - TileEntityRendererDispatcher.staticPlayerZ,
                            partialTicks, -1, 1.0F);
                }
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            } else if (FallingBlockConfig.fixMC114286 || entity.world.getBlockState(mutablePos.setPos(entity)) != state) {
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                if (this.renderOutlines) {
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                }
                bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
                mutablePos.setPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);
                GlStateManager.translate((x - (double) mutablePos.getX() - 0.5D), (y - (double) mutablePos.getY()), (z - (double) mutablePos.getZ() - 0.5D));
                BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                dispatcher.getBlockModelRenderer().renderModel(entity.world, dispatcher.getModelForState(state), state, mutablePos, bufferbuilder, false,
                        MathHelper.getPositionRandom(entity.getOrigin()));
                tessellator.draw();
                if (this.renderOutlines) {
                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();
                }
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        }

    }

}
