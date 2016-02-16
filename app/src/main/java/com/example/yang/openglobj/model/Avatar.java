package com.example.yang.openglobj.model;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import com.example.yang.openglobj.R;
import com.example.yang.openglobj.parser.ObjParser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by yang on 28/01/16.
 */
public class Avatar extends BaseObject3D {

    private static final String TAG = "Avatar";

    public int getMvpMatrixUniform() {
        return mvpMatrixUniform;
    }

    public int getMvMatrixUniform() {
        return mvMatrixUniform;
    }

    int mAvatarPositionsBufferIdx;
    int mAvatarNormalsBufferIdx;
    int mAvatarTexCoordsBufferIdx;

    public Avatar(Resources resources) {
        Log.d(TAG, "starting parsing obj");

        ObjParser objParser = new ObjParser(resources, R.raw.generic_avatar_obj);
        parse(objParser);

        Log.d(TAG, "parsing obj finished");
    }

    public void genHandle(int program, int textureHandle) {
        genHandle(program);

        // Set the active texture unit to texture unit 1.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 1.
        GLES20.glUniform1i(mTextureUniformHandle, 1);
        GLES20.glUniform1i(materialHandle, 1);
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

        mAvatarPositionsBufferIdx = buffers[0];
        mAvatarNormalsBufferIdx = buffers[1];
        mAvatarTexCoordsBufferIdx = buffers[2];

        vertexBuffer.limit(0);
        vertexBuffer = null;
        normalBuffer.limit(0);
        normalBuffer = null;
        textureBuffer.limit(0);
        textureBuffer = null;
    }

    public void draw() {
        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mAvatarPositionsBufferIdx);
        GLES20.glEnableVertexAttribArray(positionAttribute);
        GLES20.glVertexAttribPointer(positionAttribute, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0);

        // Pass in the normal information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mAvatarNormalsBufferIdx);
        GLES20.glEnableVertexAttribArray(normalAttribute);
        GLES20.glVertexAttribPointer(normalAttribute, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0);

        // Pass in the texture information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mAvatarTexCoordsBufferIdx);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, TEXCOORD_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
                0, 0);

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Draw the cubes.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, aIndices.length);
    }

    public void release() {
        // Delete buffers from OpenGL's memory
        final int[] buffersToDelete = new int[]{mAvatarPositionsBufferIdx, mAvatarNormalsBufferIdx,
                mAvatarTexCoordsBufferIdx};
        GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}
