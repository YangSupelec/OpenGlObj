package com.example.yang.openglobj.renderer;

import android.opengl.Matrix;

import com.example.yang.openglobj.phone.MainActivity;

/**
 * Created by yang on 02/02/16.
 */
public class GestureBasicRenderer extends BasicRenderer {

    public volatile float deltaX;
    public volatile float deltaY;

    public GestureBasicRenderer(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    protected void handleGesture() {
        // Draw the avatar.
        // Translate the avatar into the screen.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(modelMatrix, 0, 2.5f, 2.5f, 2.5f);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.12f, 0.0f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(currentRotation, 0);
        Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
        deltaX = 0.0f;
        deltaY = 0.0f;
    }
}
