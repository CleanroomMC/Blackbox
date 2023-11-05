package com.cleanroommc.blackbox.pipeline.rendering.meshing.thread;

import com.cleanroommc.blackbox.config.category.mesh.ChunkMeshingConfig;

public class ChunkMeshThread extends Thread {

    private Object[] localObjects = new Object[4];

    public ChunkMeshThread(Runnable runnable) {
        super(runnable);
        setPriority(ChunkMeshingConfig.threadPriority);
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

}
