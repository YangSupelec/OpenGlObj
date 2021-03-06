package com.example.yang.openglobj.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.yang.openglobj.R;
import com.example.yang.openglobj.unity.Avatar;
import com.example.yang.openglobj.unity.Hair;
import com.example.yang.openglobj.phone.HomeActivity;
import com.example.yang.openglobj.scene.BaseSurfaceView;
import com.example.yang.openglobj.util.ShaderHelper;
import com.example.yang.openglobj.util.TextResourceReader;
import com.example.yang.openglobj.util.TextureHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BasicRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "BasicRenderer";

    private final HomeActivity mainActivity;

    protected final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];

    private final float[] accumulatedRotation = new float[16];
    protected final float[] currentRotation = new float[16];
    private final float[] temporaryMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    private final float[] mLightPosInWorldSpace = new float[4];

    /** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
    private final float[] mLightPosInEyeSpace = new float[4];

    /** This is a handle to our light point program. */
    private int mPointProgramHandle;

    private int program;
    private int mTextureDataHandle;
    private int mTextureDataHandleForHair;

    protected boolean isInitialize = true;
    protected boolean hasBuffer = false;

    private Avatar avatar;
    private Hair hair;

    private final ExecutorService mSingleThreadedExecutor = Executors.newSingleThreadExecutor();

    public BasicRenderer(final HomeActivity mainActivity, final BaseSurfaceView baseSurfaceView) {
        this.mainActivity = mainActivity;
        generateData();
    }

    private void generateData() {
        mSingleThreadedExecutor.submit(new GenDataRunnable());
    }

    class GenDataRunnable implements Runnable {

        GenDataRunnable() {
            Log.d(TAG, "the thread for generating avatar data is created");
        }

        @Override
        public void run() {
            try {
                avatar = new Avatar(mainActivity.getResources());
                hair = new Hair(mainActivity.getResources());
            } catch (OutOfMemoryError e) {
                if (null != avatar) {
                    avatar.release();
                    avatar = null;
                }

                if (null != hair) {
                    hair.release();
                    hair = null;
                }
                // Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
                System.gc();

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity, "Out of memory; Dalvik takes a while to clean up the memory. Please try again.\nExternal bytes allocated=", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        hasBuffer = false;
        isInitialize = true;

        initializeGLParams();
        initializeViewMatrix();

        program = ShaderHelper.linkProgram(
                ShaderHelper.compileVertexShader(
                        TextResourceReader.readTextFileFromResource(mainActivity, R.raw.base_vertex_shader)),
                ShaderHelper.compileFragmentShader(
                        TextResourceReader.readTextFileFromResource(mainActivity, R.raw.base_fragment_shader))
        );

        mPointProgramHandle = ShaderHelper.linkProgram(
                ShaderHelper.compileVertexShader(
                        TextResourceReader.readTextFileFromResource(mainActivity, R.raw.point_vertex_shader)),
                ShaderHelper.compileFragmentShader(
                        TextResourceReader.readTextFileFromResource(mainActivity, R.raw.point_fragment_shader))
        );

        // Load the texture
        mTextureDataHandle = TextureHelper.loadTexture(mainActivity, R.drawable.generic_avatar);
        mTextureDataHandleForHair = TextureHelper.loadTexture(mainActivity, R.drawable.generic_hair);

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(accumulatedRotation, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
        initializeProjectionMatrix(width, height);
    }


    private void initializeViewMatrix() {
        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.5f;
        final float eyeZ = 3.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we
        // holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera
        // position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination
        // of a model and view matrix. In OpenGL 2, we can keep track of these
        // matrices separately if we choose.
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    private void initializeGLParams() {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    private void initializeProjectionMatrix(float width, int height) {
        // Create a new perspective projection matrix. The height will stay the
        // same while the width will vary as per aspect ratio.
        final float ratio = width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 20.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        handleGesture();

        if (null != avatar && hair != null) {
            if (!hasBuffer) {
                avatar.genBuffers();
                hair.genBuffers();
                hasBuffer = true;
            }
            hair.genHandle(program, mTextureDataHandleForHair);
            initializeModelMatrix();
            hair.bindBuffer();
            handleMatrixTrans();
            hair.draw();
            /*avatar.genHandle(program, mTextureDataHandle);
            avatar.bindBuffer();*/
        }

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0);

        // Draw a point to indicate the light.
        GLES20.glUseProgram(mPointProgramHandle);
        drawLight();
    }

    protected void handleGesture() {

    }

    private void handleMatrixTrans() {
        // Multiply the current rotation by the accumulated rotation, and then
        // set the accumulated rotation to the result.
        Matrix.multiplyMM(temporaryMatrix, 0, currentRotation, 0, accumulatedRotation, 0);
        System.arraycopy(temporaryMatrix, 0, accumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(temporaryMatrix, 0, modelMatrix, 0, accumulatedRotation, 0);
        System.arraycopy(temporaryMatrix, 0, modelMatrix, 0, 16);

        // This multiplies the view matrix by the model matrix, and stores
        // the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // Pass in the modelview matrix.
        if (null != avatar && null != hair) {
            GLES20.glUniformMatrix4fv(avatar.getMvMatrixUniform(), 1, false, mvpMatrix, 0);
            GLES20.glUniformMatrix4fv(hair.getMvMatrixUniform(), 1, false, mvpMatrix, 0);
        }

        // This multiplies the modelview matrix by the projection matrix,
        // and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

        // Pass in the combined matrix.
        if (null != avatar && hair != null) {
            GLES20.glUniformMatrix4fv(avatar.getMvpMatrixUniform(), 1, false, mvpMatrix, 0);
            GLES20.glUniformMatrix4fv(hair.getMvpMatrixUniform(), 1, false, mvpMatrix, 0);
        }
    }

    private void initializeModelMatrix() {
        if (isInitialize) {
            Matrix.rotateM(currentRotation, 0, -90, 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(currentRotation, 0, 45, 0.0f, 0.0f, 1.0f);
            isInitialize = false;
        }
    }

    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}
