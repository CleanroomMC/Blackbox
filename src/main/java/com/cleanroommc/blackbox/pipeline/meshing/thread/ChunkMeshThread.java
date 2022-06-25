package com.cleanroommc.blackbox.pipeline.meshing.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkMeshThread extends Thread {

    static final AtomicInteger nextInteger = new AtomicInteger();

    private static ThreadGroup threadGroup;

    public static void primeThreads(int threadCount, Runnable target) {
        if (threadGroup == null) {
            threadGroup = new ThreadGroup("Chunk-Meshers");
            threadGroup.setDaemon(true);
        }
        for (int i = 0; i < threadCount; i++) {
            new ChunkMeshThread(target);
        }
    }

    private static int numOfThread = 0;

    private Object[] localObjects = new Object[4];

    private ChunkMeshThread(Runnable target) {
        super(threadGroup, target, "Chunk-Mesher-" + numOfThread++);
        setDaemon(true);
    }

    public <T> T getLocalValue(LocalMeshObject<T> object) {
        if (this.localObjects.length < object.index) {
            this.localObjects = new Object[object.index + 1];
        }
        Object value = this.localObjects[object.index];
        if (value == null) {
            value = object.supplier.get();
        }
        return (T) value;
    }

    public void setLocalValue(LocalMeshObject object, Object value) {
        if (this.localObjects.length < object.index) {
            this.localObjects = new Object[object.index + 1];
        }
        this.localObjects[object.index] = value;
    }

}
