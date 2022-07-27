package com.cleanroommc.blackbox.pipeline.core.mixins;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelShapes.class)
public abstract class BlockModelShapesMixin {

    @Shadow
    public abstract void registerBuiltInBlocks(Block... builtIns);

    @Redirect(method = "registerAllBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockModelShapes;registerBuiltInBlocks([Lnet/minecraft/block/Block;)V"))
    private void builtin(BlockModelShapes instance, Block[] builtIns) {
        this.registerBuiltInBlocks(Blocks.AIR, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.PISTON_EXTENSION, Blocks.CHEST, Blocks.ENDER_CHEST,
                Blocks.TRAPPED_CHEST, Blocks.SKULL, Blocks.END_PORTAL, Blocks.BARRIER, Blocks.WALL_BANNER, Blocks.STANDING_BANNER, Blocks.END_GATEWAY, Blocks.STRUCTURE_VOID,
                Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
                Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX,
                Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BED);
        // Removed WALL_SIGN + STANDING_SIGN
    }

}
