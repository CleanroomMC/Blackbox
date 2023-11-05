package com.cleanroommc.blackbox.natives.nio;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class HeapBuffers {

    public static ByteBuffer allocateByteBuffer(int size) {
        return ByteBuffer.allocate(size);
    }

    public static IntBuffer allocateIntBuffer(int size) {
        return allocateByteBuffer(size << 2).asIntBuffer();
    }

    public static FloatBuffer allocateFloatBuffer(int size) {
        return allocateByteBuffer(size << 2).asFloatBuffer();
    }

    public static ByteBuffer growByteBuffer(ByteBuffer buffer) {
        ByteBuffer copy = allocateByteBuffer(buffer.remaining());
        copy.put(buffer);
        copy.position(0);
        return copy;
    }

    public static IntBuffer growIntBuffer(IntBuffer buffer, int additionalSize) {
        IntBuffer copy = allocateIntBuffer(buffer.remaining() + additionalSize);
        copy.put(buffer);
        copy.position(0);
        return copy;
    }

    public static FloatBuffer growFloatBuffer(FloatBuffer buffer, int additionalSize) {
        FloatBuffer copy = allocateFloatBuffer(buffer.remaining() + additionalSize);
        copy.put(buffer);
        copy.position(0);
        return copy;
    }

    private HeapBuffers() { }

}
