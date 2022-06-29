package com.cleanroommc.blackbox.pipeline.meshing.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkMeshThread extends Thread {

    static final AtomicInteger nextInteger = new AtomicInteger();

    private static int numOfThread = 0;
    private static ChunkMeshThread[] currentThreads = null;

    public static void startThreads(int threadCount, int threadPriority) {
        if (currentThreads != null) {
            throw new IllegalStateException("There are ongoing ChunkMeshThreads.");
        }
        numOfThread = 0;
        currentThreads = new ChunkMeshThread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            new ChunkMeshThread(threadPriority);
        }
    }

    public static void stopThreads() {
        for (ChunkMeshThread thread : currentThreads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) { }
        }
        currentThreads = null;
    }

    private Object[] localObjects = new Object[4];

    private ChunkMeshThread(int threadPriority) {
        super((Runnable) null, "Chunk-Mesher #" + numOfThread++);
        setPriority(threadPriority);
        setDaemon(true);
        setUncaughtExceptionHandler(ChunkMeshThreadUncaughtExceptionHandler.INSTANCE);
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

    @Override
    public void run() {

    }

}
