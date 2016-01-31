package com.example.yang.openglobj.phone;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yang.openglobj.R;
import com.example.yang.openglobj.renderer.BasicRenderer;
import com.example.yang.openglobj.scene.BaseSurfaceView;

public class MainActivity extends AppCompatActivity {

    /**
     * Hold a reference to our GLSurfaceView
     */
    private BaseSurfaceView glSurfaceView;
    private BasicRenderer renderer;
    private boolean rendererSet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        glSurfaceView = (BaseSurfaceView) findViewById(R.id.gl_surface_view);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();

        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            // Set the renderer to our demo renderer, defined below.
            renderer = new BasicRenderer(this, glSurfaceView);
            glSurfaceView.setRenderer(renderer, displayMetrics.density);
            rendererSet = true;
        }

        findViewById(R.id.button_decrease_num_cubes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseCubeCount();
            }
        });

        findViewById(R.id.button_increase_num_cubes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseCubeCount();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    private void decreaseCubeCount() {
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.decreaseCubeCount();
            }
        });
    }

    private void increaseCubeCount() {
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.increaseCubeCount();
            }
        });
    }
}
