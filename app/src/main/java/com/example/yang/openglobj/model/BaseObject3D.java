package com.example.yang.openglobj.model;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by yang on 29/01/16.
 */
public class BaseObject3D {
    /**
     * How many bytes per float.
     */
    protected final int BYTES_PER_FLOAT = 4;
    /**
     * How many bytes per float.
     */
    protected final int BYTES_PER_INT = 4;

    /**
     * Additional constants.
     */
    protected static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    protected static final int NORMAL_DATA_SIZE_IN_ELEMENTS = 3;
    protected static final int COLOR_DATA_SIZE_IN_ELEMENTS = 4;
    protected static final int TEXCOORD_DATA_SIZE_IN_ELEMENTS = 2;

    public float[] getVertices() {
        return aVertices;
    }

    public float[] getTexCoords() {
        return aTexCoords;
    }

    public float[] getNormals() {
        return aNormals;
    }

    public int[] getIndices() {
        return aIndices;
    }

    protected float[] aVertices;
    protected float[] aTexCoords;
    protected float[] aNormals;
    protected float[] aColors;
    protected int[] aIndices;

    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureBuffer;
    protected FloatBuffer colorBuffer;

    protected void assignAttribute (int position, int color, int normal, int texture) {
        // Pass in the position information
        GLES20.glVertexAttribPointer(position, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(position);

        // Pass in the color information
        GLES20.glVertexAttribPointer(color, COLOR_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
                0, colorBuffer);

        GLES20.glEnableVertexAttribArray(color);

        // Pass in the normal information
        GLES20.glVertexAttribPointer(normal, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
                0, normalBuffer);

        GLES20.glEnableVertexAttribArray(normal);

        // Pass in the texture coordinate information
        GLES20.glVertexAttribPointer(texture, TEXCOORD_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
                0, textureBuffer);

        GLES20.glEnableVertexAttribArray(texture);
    }
}
