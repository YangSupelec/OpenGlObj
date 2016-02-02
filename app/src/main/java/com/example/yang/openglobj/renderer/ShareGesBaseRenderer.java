package com.example.yang.openglobj.renderer;

import android.graphics.Bitmap;

import com.example.yang.openglobj.phone.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yang on 02/02/16.
 */
public class ShareGesBaseRenderer extends GestureBasicRenderer{
    private static final String TAG = "ShareRenderer";

    private boolean screenShot;
    private File file;
    private int width, height;

    public ShareGesBaseRenderer(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        if (screenShot) {
            takePicture(glUnused);
            screenShot = false;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        this.width = width;
        this.height = height;
    }

    private void takePicture(GL10 gl) {

        Bitmap bmp = takeScreenshot(gl);
        if (bmp != null) {
            try {
                FileOutputStream os = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
            }
        }
    }

    public Bitmap takeScreenshot(GL10 gl) {
        int mViewportWidth = width;
        int mViewportHeight = height;
        int screenshotSize = mViewportWidth * mViewportHeight;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());
        gl.glReadPixels(0, 0, mViewportWidth, mViewportHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);
        bb = null;
        Bitmap bitmap = Bitmap.createBitmap(mViewportWidth, mViewportHeight, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixelsBuffer, screenshotSize - mViewportWidth, -mViewportWidth, 0, 0, mViewportWidth, mViewportHeight);
        pixelsBuffer = null;

        short sBuffer[] = new short[screenshotSize];
        ShortBuffer sb = ShortBuffer.wrap(sBuffer);
        bitmap.copyPixelsToBuffer(sb);

        //Making created bitmap (from OpenGL points) compatible with Android bitmap
        for (int i = 0; i < screenshotSize; ++i) {
            short v = sBuffer[i];
            sBuffer[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
        }
        sb.rewind();
        bitmap.copyPixelsFromBuffer(sb);
        return bitmap;
    }

    public void screenShot(File file) {
        screenShot = true;
        this.file = file;
    }
}
