package com.cleanroommc.blackbox.pipeline.meshing.thread;

import java.util.function.Supplier;

public class LocalMeshObject<T> {

    public static <T> LocalMeshObject<T> with(Supplier<T> supplier) {
        return new LocalMeshObject<>(supplier);
    }

    final int index;
    final Supplier<T> supplier;

    private LocalMeshObject(Supplier<T> supplier) {
        if (ChunkMeshThread.nextInteger.get() == Integer.MAX_VALUE) {
            throw new RuntimeException("Reached limited amount of LocalMeshObjects");
        }
        this.index = ChunkMeshThread.nextInteger.getAndIncrement();
        this.supplier = supplier;
    }

    public T get() {
        return getThread().getLocalValue(this);
    }

    public void set(T object) {
        getThread().setLocalValue(this, object);
    }

    private ChunkMeshThread getThread() {
        Thread thread = Thread.currentThread();
        if (thread instanceof ChunkMeshThread) {
            return (ChunkMeshThread) thread;
        }
        throw new RuntimeException("Not being called from a ChunkMeshThread.");
    }

}
