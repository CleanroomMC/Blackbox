package com.cleanroommc.blackbox.pipeline.rendering.meshing.thread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class LocalMeshObject<T> {

    private static final AtomicInteger ID = new AtomicInteger();

    public static <T> LocalMeshObject<T> with(Supplier<T> supplier) {
        return new LocalMeshObject<>(supplier);
    }

    final int index;
    final Supplier<T> supplier;

    ThreadLocal<T> mainThreadLocal;

    private LocalMeshObject(Supplier<T> supplier) {
        if (ID.get() == Integer.MAX_VALUE) {
            throw new RuntimeException("Reached limited amount of LocalMeshObjects");
        }
        this.index = ID.getAndIncrement();
        this.supplier = supplier;
    }

    public T get() {
        Thread thread = getThread();
        return thread == null ? this.mainThreadLocal.get() : ((ChunkMeshThread) thread).getLocalValue(this);
    }

    public void set(T object) {
        Thread thread = getThread();
        if (thread == null) {
            this.mainThreadLocal.set(object);
        } else {
            ((ChunkMeshThread) thread).setLocalValue(this, object);
        }
    }

    private Thread getThread() {
        Thread thread = Thread.currentThread();
        if (thread instanceof ChunkMeshThread) {
            return thread;
        }
        if (this.mainThreadLocal == null) {
            this.mainThreadLocal = ThreadLocal.withInitial(supplier);
        }
        return null;
    }

}
