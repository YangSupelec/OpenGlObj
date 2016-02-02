package com.example.yang.openglobj.renderer;

import android.opengl.Matrix;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.example.yang.openglobj.phone.MainActivity;

/**
 * Created by yang on 02/02/16.
 */
public class GestureBasicRenderer extends BasicRenderer {
    private static final String TAG = "GestureRenderer";

    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetectorCompat mGestureDetector;

    public volatile float deltaX;
    public volatile float deltaY;

    public GestureBasicRenderer(MainActivity mainActivity) {
        super(mainActivity);
        mScaleDetector = new ScaleGestureDetector(mainActivity, new ScaleListener());
        mGestureDetector = new GestureDetectorCompat(mainActivity, new GestureListener());
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

    public void onTouchEvent(MotionEvent me) {

        Log.d(TAG, "on touch event");
        // check the status of avatar
        if (!rendererFinished())
            return;

        if (me.getPointerCount() == 1)
            mGestureDetector.onTouchEvent(me);
        else
            mScaleDetector.onTouchEvent(me);
    }

    private boolean rendererFinished() {
        return hasBuffer;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG, "on scale event");
            float scaleFactor = detector.getScaleFactor();

            float diff;
            if (scaleFactor > 1f)
                diff = 0.97f;
            else
                diff = 1.03f;
            try {
                Thread.sleep(20);
            } catch (Exception e) {
            }

            return false;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            Log.d(TAG, "on gesture event");
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
            } else {
            }
            try {
                Thread.sleep(5);
            } catch (Exception e) {
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "on double tap event");
            return true;
        }
    }
}
