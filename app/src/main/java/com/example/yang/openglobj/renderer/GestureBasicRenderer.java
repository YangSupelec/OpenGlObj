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

    private static final float MIN_SCALE = 0.8f;
    private static final float MAX_SCALE = 1.5f;
    private static final float LIMITE_SLIDE = 1.6f;
    private static final int RATE_SLIDE = 1000;
    private float SUM_ROTATE = 0;

    public volatile float mRotate;
    public volatile float mSlide;
    public volatile float mScale = 1.0f;

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
        Matrix.scaleM(modelMatrix, 0, 2.5f * mScale, 2.5f * mScale, 2.5f * mScale);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.12f + mSlide, 0.0f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(currentRotation, 0);
        Matrix.rotateM(currentRotation, 0, mRotate, 0.0f, 1.0f, 0.0f);
        mRotate = 0.0f;
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
            if (scaleFactor < 1f)
                diff = 0.97f;
            else
                diff = 1.03f;
            mScale = Math.min(mScale * diff, MAX_SCALE);
            mScale = Math.max(MIN_SCALE, mScale);
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
                mRotate = -distanceX;
                SUM_ROTATE += mRotate;
            } else {
                mSlide += distanceY / (RATE_SLIDE * mScale);
                mSlide = Math.max(mSlide, -LIMITE_SLIDE / mScale);
                mSlide = Math.min(mSlide, LIMITE_SLIDE / mScale);
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
            mSlide = 0;
            mScale = 1;
            mRotate = -SUM_ROTATE;
            return true;
        }
    }
}
