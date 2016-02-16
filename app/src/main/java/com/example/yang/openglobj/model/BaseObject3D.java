package com.example.yang.openglobj.model;

import android.opengl.GLES20;

import com.example.yang.openglobj.parser.ObjParser;

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
    protected static final String TEX_COORD_UNIFORM_HAIR = "u_Texture_Hair";
    protected static final String TEX_COORD_UNIFORM_AVATAR = "u_Texture_Avatar";
    protected static final String TEX_COORD_UNIFORM_TYPE = "u_Material";

    protected static final String POSITION_ATTRIBUTE = "a_Position";
    protected static final String COLOR_ATTRIBUTE = "a_Color";
    protected static final String NORMAL_ATTRIBUTE = "a_Normal";
    protected static final String TEX_COORD_ATTRIBUTE = "a_TexCoordinate";

    protected float[] aVertices;
    protected float[] aTexCoords;
    protected float[] aNormals;
    protected float[] aColors;
    protected int[] aIndices;

    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureBuffer;
    protected FloatBuffer colorBuffer;

    /**
     * OpenGL handles to our program uniforms.
     */
    protected int mvpMatrixUniform;
    protected int mvMatrixUniform;

    /**
     * OpenGL handles to our program attributes.
     */
    protected int positionAttribute;
    protected int normalAttribute;
    protected int colorAttribute;

    /**
     * This will be used to pass in the texture.
     */
    protected int mTextureUniformHandle;
    protected int mTextureCoordinateHandle;
    protected int materialHandle;

    protected int mPositionsBufferIdx;
    protected int mNormalsBufferIdx;
    protected int mTexCoordsBufferIdx;

    protected void parse(ObjParser objParser) {
        objParser.parse();
        aVertices = objParser.getVertices();
        aTexCoords = objParser.getTexCoords();
        aNormals = objParser.getNormals();
        aIndices = objParser.getIndices();
    }

    protected void genHandle(int program) {
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(program);

        // Set program handles for model drawing.
        mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
        mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
        positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
        normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
        mTextureUniformHandle = GLES20.glGetUniformLocation(program, TEX_COORD_UNIFORM_AVATAR);
        materialHandle = GLES20.glGetUniformLocation(program, TEX_COORD_UNIFORM_TYPE);
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(program, TEX_COORD_ATTRIBUTE);
    }
}
