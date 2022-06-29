package com.cleanroommc.blackbox.pipeline.meshing.thread;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;

import java.lang.Thread.UncaughtExceptionHandler;

public enum ChunkMeshThreadUncaughtExceptionHandler implements UncaughtExceptionHandler {

    INSTANCE;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Minecraft.getMinecraft().addScheduledTask(() -> CrashReport.makeCrashReport(e, "Chunk Meshing"));
    }

}
