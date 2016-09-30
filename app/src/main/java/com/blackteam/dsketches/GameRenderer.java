package com.blackteam.dsketches;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Класс Рендеринга.
 */
public class GameRenderer implements GLSurfaceView.Renderer {
    private Context context_;

    /** Model View Projection Matrix. */
    private final float[] mMVPMatrix_ = new float[16];
    private final float[] mProjectionMatrix_ = new float[16];
    private final float[] mViewMatrix_ = new float[16];

    private ShaderProgram shader_;
    private GameScreen gameScreen_;

    public GameRenderer(Context context) {
        this.context_ = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        configRender();
        initCamera();
        createShader();

        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        try {
            GLES20.glViewport(0, 0, width, height);

            float aspectRatio = width > height ?
                    (float) width / height :
                    (float) height / width;

            // Landscape.
            if (width > height) {
                // this projection matrix is applied to object coordinates
                // in the onDrawFrame() method
                Matrix.orthoM(mProjectionMatrix_, 0,
                        0, aspectRatio,
                        0, 1,
                        0.3f, // near.
                        3f // far.
                );

                throw new Exception("Landscape is not available.");
            }
            // Portrait or square.
            else {
                // this projection matrix is applied to object coordinates
                // in the onDrawFrame() method
                Matrix.orthoM(mProjectionMatrix_, 0,
                        0, 1, // left-right;
                        0, aspectRatio, // top-bottom;
                        0.3f, // near.
                        3f // far.
                );

                if (gameScreen_ == null) {
                    gameScreen_ = new GameScreen(1f, aspectRatio, shader_);
                    gameScreen_.loadContent(context_);
                    gameScreen_.init();
                }
                else {
                    //gameScreen_.rebuild(2f, 2 * aspectRatio);
                }
            }

            // Calculate the projection and view transformation.
            Matrix.multiplyMM(mMVPMatrix_, 0, mProjectionMatrix_, 0, mViewMatrix_, 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context_, "Internal error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i("FPS", String.valueOf(FPSCounter.logFrame()));

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        gameScreen_.onDraw(mMVPMatrix_);
    }

    /**
     * Настройка свйоств рендеринга.
     */
    private void configRender() {
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
    }

    private void initCamera() {
        // Set the camera position.
        Matrix.setLookAtM(mViewMatrix_, 0,
                0f, 0f, 1f, // eye. Положение точки наблюдения в пространстве.
                0f, 0f, 0f,  // look. Координаты куда смотреть.
                0f, 1f, 0f   // up-vector смотрит вверх, вдоль оси Y.
        );
    }

    private void createShader() {
        shader_ = new ShaderProgram();
        boolean isShaderCompiled = shader_.compile();
        if (!isShaderCompiled) throw new IllegalArgumentException("Error compiling shader.");
        shader_.begin();
    }

}
