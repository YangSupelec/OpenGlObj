package com.example.yang.openglobj.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.example.yang.openglobj.R;
import com.example.yang.openglobj.model.Cube;
import com.example.yang.openglobj.model.Star;
import com.example.yang.openglobj.phone.MainActivity;
import com.example.yang.openglobj.util.ErrorHandler;
import com.example.yang.openglobj.util.ShaderHelper;
import com.example.yang.openglobj.util.TextResourceReader;
import com.example.yang.openglobj.util.TextureHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yang on 28/01/16.
 */
public class BasicRenderer implements GLSurfaceView.Renderer {
    /**
     * Used for debug logs.
     */
    private static final String TAG = "BasicRenderer";

    /**
     * References to other main objects.
     */
    private final MainActivity mainActivity;
    private final GLSurfaceView mGlSurfaceView;

    /**
     * Store the model matrix. This matrix is used to move models from object
     * space (where each model can be thought of being located at the center of
     * the universe) to world space.
     */
    private final float[] modelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix
     * transforms world space to eye space; it positions things relative to our
     * eye.
     */
    private final float[] viewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D
     * viewport.
     */
    private final float[] projectionMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into
     * the shader program.
     */
    private final float[] mvpMatrix = new float[16];

    /**
     * Additional matrices.
     */
    private final float[] accumulatedRotation = new float[16];
    private final float[] currentRotation = new float[16];
    private final float[] temporaryMatrix = new float[16];


    /**
     * OpenGL handles to our program uniforms.
     */
    private int mvpMatrixUniform;
    private int mvMatrixUniform;

    /** This will be used to pass in the light position. */
    private int mLightPosHandle;

    /**
     * OpenGL handles to our program attributes.
     */
    private int positionAttribute;
    private int normalAttribute;
    private int colorAttribute;

    /**
     * This will be used to pass in the texture.
     */
    private int mTextureUniformHandle;
    /**
     * This will be used to pass in model texture coordinate information.
     */
    private int mTextureCoordinateHandle;

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    private final float[] mLightPosInWorldSpace = new float[4];

    /** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
    private final float[] mLightPosInEyeSpace = new float[4];

    /** Additional info for cube generation. */
    private int mLastRequestedCubeFactor;
    private int mActualCubeFactor;

    /**
     * This is a handle to our cube shading program.
     */
    private int program;

    /** This is a handle to our texture data. */
    private int mTextureDataHandle;

    /**
     * Identifiers for our uniforms and attributes inside the shaders.
     */
    private static final String MVP_MATRIX_UNIFORM = "u_MVPMatrix";
    private static final String MV_MATRIX_UNIFORM = "u_MVMatrix";
    private static final String TEX_COORD_UNIFORM = "u_Texture";
    private static final String LIGHT_UNIFORM = "u_LightPos";

    private static final String POSITION_ATTRIBUTE = "a_Position";
    private static final String COLOR_ATTRIBUTE = "a_Color";
    private static final String NORMAL_ATTRIBUTE = "a_Normal";
    private static final String TEX_COORD_ATTRIBUTE = "a_TexCoordinate";

    /**
     * Retain the most recent delta for touch events.
     */
    // These still work without volatile, but refreshes are not guaranteed to
    // happen.
    public volatile float deltaX;
    public volatile float deltaY;

    protected boolean isInitialize = true;

    /**
     * The current draw object.
     */
    private Star star;

    /** Thread executor for generating cube data in the background. */
    private final ExecutorService mSingleThreadedExecutor = Executors.newSingleThreadExecutor();

    /**
     * Initialize the model data.
     */
    public BasicRenderer(final MainActivity mainActivity, final GLSurfaceView glSurfaceView) {
        this.mainActivity = mainActivity;
        this.mGlSurfaceView = glSurfaceView;
    }

    private void generateAvatarData() {
        mSingleThreadedExecutor.submit(new GenDataRunnable());
    }

    class GenDataRunnable implements Runnable {

        GenDataRunnable() {
            Log.d(TAG, "the thread for generating avatar data is created");
        }

        @Override
        public void run() {
            try {
                star = new Star(mainActivity.getResources());
                // Run on the GL thread -- the same thread the other members of the renderer run in.
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        // Not supposed to manually call this, but Dalvik sometimes needs some additional prodding to clean up the heap.
                        System.gc();

                        try {
                            star.genBuffers();
                        } catch (OutOfMemoryError err) {
                            if (star != null) {
                                star.release();
                                star = null;
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
                });
            } catch (OutOfMemoryError e) {
                if (null != star) {
                    star.release();
                    star = null;
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

    public void decreaseCubeCount() {
    }

    public void increaseCubeCount() {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        generateAvatarData();

        // Set the background clear color to black.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

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

        program = ShaderHelper.linkProgram(
                ShaderHelper.compileVertexShader(
                        TextResourceReader.readTextFileFromResource(mainActivity, R.raw.avatar_vertex_shader)),
                ShaderHelper.compileFragmentShader(
                        TextResourceReader.readTextFileFromResource(mainActivity, R.raw.avatar_fragment_shader))
        );

        // Load the texture
        mTextureDataHandle = TextureHelper.loadTexture(mainActivity, R.drawable.generic_avatar);

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(accumulatedRotation, 0);
    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     *
     * @param width  The new width, in pixels.
     * @param height The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the
        // same while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 20.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(program);

        // Set program handles for cube drawing.
        mvpMatrixUniform = GLES20.glGetUniformLocation(program, MVP_MATRIX_UNIFORM);
        mvMatrixUniform = GLES20.glGetUniformLocation(program, MV_MATRIX_UNIFORM);
        mLightPosHandle = GLES20.glGetUniformLocation(program, LIGHT_UNIFORM);
        positionAttribute = GLES20.glGetAttribLocation(program, POSITION_ATTRIBUTE);
        normalAttribute = GLES20.glGetAttribLocation(program, NORMAL_ATTRIBUTE);
        mTextureUniformHandle = GLES20.glGetUniformLocation(program, TEX_COORD_UNIFORM);
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(program, TEX_COORD_ATTRIBUTE);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -1.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0);

        // Draw the avatar.
        // Translate the avatar into the screen.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(modelMatrix, 0, 3.0f, 3.0f, 3.0f);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.12f, 0.0f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(currentRotation, 0);
        Matrix.rotateM(currentRotation, 0, deltaX, 0.0f, 1.0f, 0.0f);
        if (isInitialize) {
            Matrix.rotateM(currentRotation, 0, -90, 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(currentRotation, 0, 45, 0.0f, 0.0f, 1.0f);
            isInitialize = false;
        }
        deltaX = 0.0f;
        deltaY = 0.0f;

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
        GLES20.glUniformMatrix4fv(mvMatrixUniform, 1, false, mvpMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix,
        // and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(temporaryMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        System.arraycopy(temporaryMatrix, 0, mvpMatrix, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mvpMatrixUniform, 1, false, mvpMatrix, 0);

        if (null != star) {
            star.draw(positionAttribute, normalAttribute, mTextureCoordinateHandle);
        }
    }
}
