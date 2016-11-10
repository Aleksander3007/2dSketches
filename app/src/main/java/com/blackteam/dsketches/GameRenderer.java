package com.blackteam.dsketches;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.utils.GameMath;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Класс Рендеринга.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    public static float width = 0;
    public static float height = 0;

    // Units per pixels.
    public static float uppX = 1.0f;
    public static float uppY = 1.0f;

    private Context context_;

    /** Model View Projection Matrix. */
    private final float[] mvpMatrix_ = new float[16];
    private final float[] projectionMatrix_ = new float[16];
    private final float[] viewMatrix_ = new float[16];

    private ShaderProgram shader_;

    /**
     * Ограничение по FPS.
     * Это позволительно, потому что для игры не критично значение FPS (как, например, для шутера).
     */
    private long MS_PER_FRAME_ = 33; //ms; ~ 30.3 FPS.
    /** мс. */
    private long currentTime_;
    /** Время последенго обновления, мс. */
    private long lastTime_;
    /** Сколько времени прошло с последнего обновления, мс.. */
    private long elapsedTime_;

    private Graphics graphics_;
    private Game game_;
    private ContentManager contents_;

    public GameRenderer(Context context, Game game, ContentManager contents) {
        this.context_ = context;
        this.game_ = game;
        this.contents_ = contents;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        loadContent();

        configRender();
        initCamera();
        createShader();

        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);

        lastTime_ = GameMath.getCurrentTime();

        graphics_ = new Graphics(mvpMatrix_, shader_);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i("GameRender", "onSurfaceChanged begin");
        GLES20.glViewport(0, 0, width, height);
        GameRenderer.width = width;
        GameRenderer.height = height;

        float aspectRatio = width > height ?
                (float) width / height :
                (float) height / width;

        // Landscape.
        if (width > height) {
            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.orthoM(projectionMatrix_, 0,
                    0, aspectRatio,
                    0, 1,
                    0.3f, // near.
                    3f // far.
            );

            uppX = aspectRatio / width;
            uppY = 1.0f / height;

            throw new Error("Landscape is not available.");
        }
        // Portrait or square.
        else {
            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.orthoM(projectionMatrix_, 0,
                    0, 1, // left-right;
                    0, aspectRatio, // top-bottom;
                    0.3f, // near.
                    3f // far.
            );

            uppX = 1.0f / width;
            uppY = aspectRatio / height;

            game_.resize(1f, aspectRatio);
        }

        // Calculate the projection and view transformation.
        Matrix.multiplyMM(mvpMatrix_, 0, projectionMatrix_, 0, viewMatrix_, 0);

        Log.i("GameRender", "onSurfaceChanged end");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        currentTime_ = GameMath.getCurrentTime();
        elapsedTime_ = currentTime_ - lastTime_;
        lastTime_ = currentTime_;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        graphics_.setElapsedTime(elapsedTime_);
        game_.render(graphics_);

        elapsedTime_ = GameMath.getCurrentTime() - lastTime_;
        // Игра работает с (1/MS_PER_FRAME) FPS, для сохранности батареи, для меньшей нагрузки проца.
        // Это позволительно, потому что для игры не критично значение FPS (как, например, для шутера).
        if (elapsedTime_ < MS_PER_FRAME_) {
            try {
                Thread.sleep(MS_PER_FRAME_ - elapsedTime_);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        //Log.i("FPS", String.valueOf(FPSCounter.logFrame()));
    }

    /**
     * Настройка свойств рендеринга.
     */
    private void configRender() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.i("Config Render", "error = " + String.valueOf(error));
            throw new Error("Configuration render: error = " + String.valueOf(error));
        }
    }

    private void initCamera() {
        // Set the camera position.
        Matrix.setLookAtM(viewMatrix_, 0,
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

    private void loadContent() {
        // TODO: Посмотреть нельзя как нибудь одной функцией грузить! R.drawable.<Загрузка всего>.
        contents_.load(R.drawable.achievement_bg_noactive);
        contents_.load(R.drawable.achievement_btn);
        contents_.load(R.drawable.achievement_window_bg);
        contents_.load(R.drawable.chasm);
        contents_.load(R.drawable.error);
        contents_.load(R.drawable.exit_btn);
        contents_.load(R.drawable.menu_window);
        contents_.load(R.drawable.numbers);
        contents_.load(R.drawable.profit_numbers);
        contents_.load(R.drawable.reshuffle);
        contents_.load(R.drawable.restart_btn);
        contents_.load(R.drawable.dots_selector);
        contents_.load(R.drawable.x_close);
        contents_.load(R.drawable.dots_theme1);
        contents_.load(R.drawable.anim_spec_roweater);
        contents_.load(R.drawable.effect_roweater);
        contents_.load(R.drawable.skills);
    }
}
