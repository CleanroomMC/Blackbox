package com.cleanroommc.blackbox.pipeline.rendering.meshing;

import com.cleanroommc.blackbox.config.category.mesh.ChunkMeshingConfig;
import com.cleanroommc.blackbox.pipeline.rendering.meshing.thread.ChunkMeshThread;
import com.cleanroommc.blackbox.pipeline.rendering.meshing.thread.ChunkMeshThreadUncaughtExceptionHandler;
import com.cleanroommc.blackbox.pipeline.rendering.meshing.work.AbstractMeshWork;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MeshWorkDispatcher {

    private final AtomicBoolean stopFlag;
    private final ExecutorService workerService;

    public MeshWorkDispatcher() {
        this.stopFlag = new AtomicBoolean(false);
        this.workerService = Executors.newFixedThreadPool(ChunkMeshingConfig.threadCount, new BasicThreadFactory.Builder()
                .wrappedFactory(ChunkMeshThread::new)
                .daemon(true)
                .priority(ChunkMeshingConfig.threadPriority)
                .uncaughtExceptionHandler(ChunkMeshThreadUncaughtExceptionHandler.INSTANCE)
                .namingPattern("Mesh Worker #%s")
                .build());
    }

    public void submitWork(AbstractMeshWork meshWork) {
        this.workerService.submit(meshWork::work);
    }

    public void forciblyFinish() {
        this.stopFlag.set(true);
        this.workerService.shutdownNow();
    }

    public boolean hasStopped() {
        return stopFlag.get();
    }

}
