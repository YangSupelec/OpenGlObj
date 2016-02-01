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

    /**
     * Identifiers for our uniforms and attributes inside the shaders.
     */
    protected static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
    protected static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
    protected static final String TEX_COORD_UNIFORM = "u_Texture";

    protected static final String POSITION_ATTRIBUTE = "a_Position";
    private static final String COLOR_ATTRIBUTE = "a_Color";
    protected static final String NORMAL_ATTRIBUTE = "a_Normal";
    protected static final String TEX_COORD_ATTRIBUTE = "a_TexCoordinate";
}
