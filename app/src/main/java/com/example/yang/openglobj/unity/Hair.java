package com.example.yang.openglobj.unity;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.example.yang.openglobj.R;
import com.example.yang.openglobj.parser.ObjParser;

/**
 * Created by yang on 28/01/16.
 */
public class Hair extends BaseObject3D {
    private static final String TAG = "Hair";

    public Hair(Resources resources) {
        ObjParser objParser = new ObjParser(resources, R.raw.generic_hair_obj);
        super.parse(objParser);
    }

    public void genHandle(int program, int textureHandle) {
        super.genHandle(program);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glUniform1i(materialHandle, 0);
    }
}
