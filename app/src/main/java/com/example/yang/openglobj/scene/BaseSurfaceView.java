package com.example.yang.openglobj.scene;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.yang.openglobj.R;
import com.example.yang.openglobj.renderer.ShareGesBaseRenderer;
import com.example.yang.openglobj.util.ErrorHandler;

/**
 * Created by yang on 29/01/16.
 */
public class BaseSurfaceView extends GLSurfaceView implements ErrorHandler {
    private ShareGesBaseRenderer basicRenderer;

    private float density;

    public BaseSurfaceView(Context context) {
        super(context);
    }

    public BaseSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void handleError(final ErrorType errorType, final String cause) {
        // Queue on UI thread.
        post(new Runnable() {
            @Override
            public void run() {
                final String text;

                switch (errorType) {
                    case BUFFER_CREATION_ERROR:
                        text = String
                                .format(getContext().getResources().getString(
                                        R.string.error_could_not_create_vbo), cause);
                        break;
                    default:
                        text = String.format(
                                getContext().getResources().getString(
                                        R.string.error_unknown), cause);
                }

                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (basicRenderer != null)
            basicRenderer.onTouchEvent(event);
        return true;
    }

    // Hides superclass method.
    public void setRenderer(ShareGesBaseRenderer renderer, float density)
    {
        this.basicRenderer = renderer;
        this.density = density;
        super.setRenderer(renderer);
    }
}
