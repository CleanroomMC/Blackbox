package com.cleanroommc.blackbox.natives.nio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class NativeBuffers {

    public static ByteBuffer allocateByteBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer allocateIntBuffer(int size) {
        return allocateByteBuffer(size << 2).asIntBuffer();
    }

    public static FloatBuffer allocateFloatBuffer(int size) {
        return allocateByteBuffer(size << 2).asFloatBuffer();
    }

    public static ByteBuffer growByteBuffer(ByteBuffer buffer, int additionalSize) {
        ByteBuffer copy = allocateByteBuffer(buffer.remaining() + additionalSize);
        copy.put(buffer);
        copy.position(0);
        return copy;
    }

    public static ByteBuffer growByteBuffer(ByteBuffer buffer, float growFactor) {
        ByteBuffer copy = allocateByteBuffer((int) (buffer.remaining() * growFactor));
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

    public static IntBuffer growIntBuffer(IntBuffer buffer, float growFactor) {
        IntBuffer copy = allocateIntBuffer((int) (buffer.remaining() * growFactor));
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

    public static FloatBuffer growFloatBuffer(FloatBuffer buffer, float growFactor) {
        FloatBuffer copy = allocateFloatBuffer((int) (buffer.remaining() * growFactor));
        copy.put(buffer);
        copy.position(0);
        return copy;
    }

    private NativeBuffers() { }

}
