package com.cleanroommc.blackbox.pipeline.rendering.meshing;

import com.cleanroommc.blackbox.config.category.mesh.ChunkMeshingConfig;
import com.cleanroommc.blackbox.pipeline.rendering.meshing.thread.ChunkMeshThread;
import com.cleanroommc.blackbox.pipeline.rendering.meshing.thread.ChunkMeshThreadUncaughtExceptionHandler;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeshWorkDispatcher {

    private final ExecutorService workerService;

    public MeshWorkDispatcher() {
        this.workerService = Executors.newFixedThreadPool(ChunkMeshingConfig.threadCount, new BasicThreadFactory.Builder()
                .wrappedFactory(ChunkMeshThread::new)
                .daemon(true)
                .priority(ChunkMeshingConfig.threadPriority)
                .uncaughtExceptionHandler(ChunkMeshThreadUncaughtExceptionHandler.INSTANCE)
                .namingPattern("Mesh Worker #%s")
                .build());
    }



}
