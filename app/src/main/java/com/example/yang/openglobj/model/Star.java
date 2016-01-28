package com.example.yang.openglobj.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by yang on 28/01/16.
 */
public class Star {
    static final float X = .525731112119133606f;
    static final float Z = .850650808352039932f;


    /** How many bytes per float. */
    private final int BYTES_PER_FLOAT = 4;
    /** How many bytes per float. */
    private final int BYTES_PER_SHORT = 2;

    // The colors mapped to the vertices.
    float[] colors = {
            0f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f,
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f

    };

    // The normal mapped to the vertices
    float[] normals = {};

    float vertices[] = new float[]{
            -X, 0.0f, Z, X, 0.0f, Z, -X, 0.0f, -Z, X, 0.0f, -Z,
            0.0f, Z, X, 0.0f, Z, -X, 0.0f, -Z, X, 0.0f, -Z, -X,
            Z, X, 0.0f, -Z, X, 0.0f, Z, -X, 0.0f, -Z, -X, 0.0f
    };

    public short[] getIndices() {
        return indices;
    }

    short indices[] = new short[]{
            0, 4, 1, 0, 9, 4, 9, 5, 4, 4, 5, 8, 4, 8, 1,
            8, 10, 1, 8, 3, 10, 5, 3, 8, 5, 2, 3, 2, 7, 3,
            7, 10, 3, 7, 6, 10, 7, 11, 6, 11, 0, 6, 0, 1, 6,
            6, 1, 10, 9, 0, 11, 9, 11, 2, 9, 2, 5, 7, 2, 11};


    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getColorBuffer() {
        return colorBuffer;
    }

    public ShortBuffer getIndexBuffer() {
        return indexBuffer;
    }

    private FloatBuffer vertexBuffer;

    private FloatBuffer colorBuffer;

    private ShortBuffer indexBuffer;

    public FloatBuffer getNormalBuffer() {
        return normalBuffer;
    }

    private FloatBuffer normalBuffer;

    public Star() {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * BYTES_PER_FLOAT);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT);
        nbb.order(ByteOrder.nativeOrder());
        normalBuffer = nbb.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);
    }
}
