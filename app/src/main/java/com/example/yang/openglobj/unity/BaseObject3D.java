package com.example.yang.openglobj.unity;

import android.opengl.GLES20;

import com.example.yang.openglobj.parser.ObjParser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public int getMvpMatrixUniform() {
        return mvpMatrixUniform;
    }

    public int getMvMatrixUniform() {
        return mvMatrixUniform;
    }

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

    public void genBuffers() {
        ByteBuffer vbb = ByteBuffer.allocateDirect(aVertices.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(aVertices);
        vertexBuffer.position(0);

        ByteBuffer nbb = ByteBuffer.allocateDirect(aNormals.length * BYTES_PER_FLOAT);
        nbb.order(ByteOrder.nativeOrder());
        normalBuffer = nbb.asFloatBuffer();
        normalBuffer.put(aNormals);
        normalBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(aTexCoords.length * BYTES_PER_FLOAT);
        tbb.order(ByteOrder.nativeOrder());
        textureBuffer = tbb.asFloatBuffer();
        textureBuffer.put(aTexCoords);
        textureBuffer.position(0);

        final int buffers[] = new int[3];
        GLES20.glGenBuffers(3, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normalBuffer.capacity() * BYTES_PER_FLOAT, normalBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureBuffer.capacity() * BYTES_PER_FLOAT, textureBuffer,
                GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        mPositionsBufferIdx = buffers[0];
        mNormalsBufferIdx = buffers[1];
        mTexCoordsBufferIdx = buffers[2];

        vertexBuffer.limit(0);
        vertexBuffer = null;
        normalBuffer.limit(0);
        normalBuffer = null;
        textureBuffer.limit(0);
        textureBuffer = null;
    }

    public void bindBuffer() {
        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mPositionsBufferIdx);
        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0);

        // Pass in the normal information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mNormalsBufferIdx);
        GLES20.glEnableVertexAttribArray(normalAttribute);
        GLES20.glVertexAttribPointer(normalAttribute, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0);

        // Pass in the texture information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mTexCoordsBufferIdx);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXCOORD_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
                0, 0);

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void draw() {
        // Draw the cubes.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, aIndices.length);
    }

    public void release() {
        // Delete buffers from OpenGL's memory
        final int[] buffersToDelete = new int[]{mPositionsBufferIdx, mNormalsBufferIdx,
                mTexCoordsBufferIdx};
        GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}
