package com.example.yang.openglobj.model;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by yang on 29/01/16.
 */
public class BaseObject3D {
    // useful constantes
    protected final int BYTES_PER_FLOAT = 4;
    protected static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    protected static final int NORMAL_DATA_SIZE_IN_ELEMENTS = 3;
    protected static final int COLOR_DATA_SIZE_IN_ELEMENTS = 4;
    protected static final int TEXCOORD_DATA_SIZE_IN_ELEMENTS = 2;

    protected float[] aVertices;
    protected float[] aTexCoords;
    protected float[] aNormals;
    protected float[] aColors;
    protected int[] aIndices;

    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureBuffer;
    protected FloatBuffer colorBuffer;
}
