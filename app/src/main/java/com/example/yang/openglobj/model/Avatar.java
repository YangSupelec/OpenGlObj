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

    public Avatar(Resources resources) {
        ObjParser objParser = new ObjParser(resources, R.raw.generic_avatar_obj);
        super.parse(objParser);
    }

    public void genHandle(int program, int textureHandle) {
        super.genHandle(program);

        // Set the active texture unit to texture unit 1.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 1.
        GLES20.glUniform1i(mTextureUniformHandle, 1);
        GLES20.glUniform1i(materialHandle, 1);
    }

    public void draw() {
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
