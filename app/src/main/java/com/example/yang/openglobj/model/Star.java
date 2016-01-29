package com.example.yang.openglobj.model;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.example.yang.openglobj.R;
import com.example.yang.openglobj.parser.ObjParser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by yang on 28/01/16.
 */
public class Star extends BaseObject3D {

    int mCubePositionsBufferIdx;
    int mCubeNormalsBufferIdx;
    int mCubeTexCoordsBufferIdx;

    public Star(Resources resources) {
        ObjParser objParser = new ObjParser(resources, R.raw.generic_avatar_obj);
        objParser.parse();
        aVertices = objParser.getVertices();
        aTexCoords = objParser.getTexCoords();
        aNormals = objParser.getNormals();
        aIndices = objParser.getIndices();

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

        genBuffers();
    }

    private void genBuffers() {
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

        mCubePositionsBufferIdx = buffers[0];
        mCubeNormalsBufferIdx = buffers[1];
        mCubeTexCoordsBufferIdx = buffers[2];

        vertexBuffer.limit(0);
        vertexBuffer = null;
        normalBuffer.limit(0);
        normalBuffer = null;
        textureBuffer.limit(0);
        textureBuffer = null;
    }

    public void draw(int position, int normal, int texture) {
        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubePositionsBufferIdx);
        GLES20.glEnableVertexAttribArray(position);
        GLES20.glVertexAttribPointer(position, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0);

        // Pass in the normal information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeNormalsBufferIdx);
        GLES20.glEnableVertexAttribArray(normal);
        GLES20.glVertexAttribPointer(normal, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0);

        // Pass in the texture information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeTexCoordsBufferIdx);
        GLES20.glEnableVertexAttribArray(texture);
        GLES20.glVertexAttribPointer(texture, TEXCOORD_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false,
                0, 0);

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Draw the cubes.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, aIndices.length / 3);
    }
}
