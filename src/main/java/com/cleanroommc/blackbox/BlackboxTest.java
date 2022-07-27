package com.cleanroommc.blackbox;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class BlackboxTest {

    public static void construct() {
        MinecraftForge.EVENT_BUS.register(BlackboxTest.class);
    }

    public static void preInit() {

    }

    public static void postInit() {

    }

    private static boolean finishTesting = false;

    @SubscribeEvent
    public static void test(WorldTickEvent event) {
        if (event.world.getWorldType() != WorldType.FLAT) {
            MinecraftForge.EVENT_BUS.unregister(BlackboxTest.class);
        }
        if (!finishTesting && !event.world.playerEntities.isEmpty()) {
            EntityPlayer player = event.world.playerEntities.get(0);
            Chunk chunk = event.world.getChunk(player.chunkCoordX + 1, player.chunkCoordZ + 1);
            BlockPos center = chunk.getPos().getBlock(0, 0, 0);
            center = event.world.getTopSolidOrLiquidBlock(center).up();
            MutableBlockPos mutablePos = new MutableBlockPos();
            for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
                for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                    for (int y = center.getY(); y < (center.getY() + 8); y++) {
                        chunk.setBlockState(mutablePos.setPos(x, y, z), Blocks.STANDING_SIGN.getDefaultState());
                        // TileEntitySign signTE = (TileEntitySign) chunk.getTileEntity(mutablePos, EnumCreateEntityType.IMMEDIATE);
                        // signTE.signText[0] = new TextComponentString("TESTING TESTING");
                        // signTE.signText[3] = new TextComponentString("123");
                    }
                }
            }
            finishTesting = true;
        }
    }

}
