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

    public static float sWidth = 0;
    public static float sHeight = 0;

    // Units per pixels.
    public static float sUppX = 1.0f;
    public static float sUppY = 1.0f;

    // TODO: Delete unused fields.
    private Context mContext;

    /** Model View Projection Matrix. */
    private final float[] mMvpMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private ShaderProgram mShader;

    /**
     * Ограничение по FPS.
     * Это позволительно, потому что для игры не критично значение FPS (как, например, для шутера).
     */
    private static final long MS_PER_FRAME = 33; //ms; ~ 30.3 FPS.
    /** мс. */
    private long mCurrentTime;
    /** Время последенго обновления, мс. */
    private long mLastTime;
    /** Сколько времени прошло с последнего обновления, мс.. */
    private long mElapsedTime;

    private Graphics mGraphics;
    private Game mGame;
    private ContentManager mContents;

    public GameRenderer(Context context, Game game, ContentManager contents) {
        this.mContext = context;
        this.mGame = game;
        this.mContents = contents;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        loadContent();

        configRender();
        initCamera();
        createShader();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mLastTime = GameMath.getCurrentTime();

        mGraphics = new Graphics(mMvpMatrix, mShader);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i("GameRender", "onSurfaceChanged begin");
        GLES20.glViewport(0, 0, width, height);
        GameRenderer.sWidth = width;
        GameRenderer.sHeight = height;

        float aspectRatio = width > height ?
                (float) width / height :
                (float) height / width;

        // Landscape.
        if (width > height) {
            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.orthoM(mProjectionMatrix, 0,
                    0, aspectRatio,
                    0, 1,
                    0.3f, // near.
                    3f // far.
            );

            sUppX = aspectRatio / width;
            sUppY = 1.0f / height;

            mGame.resize(aspectRatio, 1f);
        }
        // Portrait or square.
        else {
            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.orthoM(mProjectionMatrix, 0,
                    0, 1, // left-right;
                    0, aspectRatio, // top-bottom;
                    0.3f, // near.
                    3f // far.
            );

            sUppX = 1.0f / width;
            sUppY = aspectRatio / height;

            mGame.resize(1f, aspectRatio);
        }



        // Calculate the projection and view transformation.
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Log.i("GameRender", "onSurfaceChanged end");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCurrentTime = GameMath.getCurrentTime();
        mElapsedTime = mCurrentTime - mLastTime;
        mLastTime = mCurrentTime;

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mGraphics.setElapsedTime(mElapsedTime);
        mGame.render(mGraphics);

        mElapsedTime = GameMath.getCurrentTime() - mLastTime;
        // Игра работает с (1/MS_PER_FRAME) FPS, для сохранности батареи, для меньшей нагрузки проца.
        // Это позволительно, потому что для игры не критично значение FPS (как, например, для шутера).
        if (mElapsedTime < MS_PER_FRAME) {
            try {
                Thread.sleep(MS_PER_FRAME - mElapsedTime);
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
        Matrix.setLookAtM(mViewMatrix, 0,
                0f, 0f, 1f, // eye. Положение точки наблюдения в пространстве.
                0f, 0f, 0f,  // look. Координаты куда смотреть.
                0f, 1f, 0f   // up-vector смотрит вверх, вдоль оси Y.
        );
    }

    private void createShader() {
        mShader = new ShaderProgram();
        boolean isShaderCompiled = mShader.compile();
        if (!isShaderCompiled) throw new IllegalArgumentException("Error compiling shader.");
        mShader.begin();
    }

    private void loadContent() {
        // TODO: Посмотреть нельзя как нибудь одной функцией грузить! R.drawable.<Загрузка всего>.
        mContents.load(R.drawable.achievement_bg_noactive);
        mContents.load(R.drawable.achievement_window_bg);
        mContents.load(R.drawable.chasm);
        mContents.load(R.drawable.error);
        mContents.load(R.drawable.exit_btn);
        mContents.load(R.drawable.menu_window);
        mContents.load(R.drawable.numbers);
        mContents.load(R.drawable.profit_numbers);
        mContents.load(R.drawable.reshuffle);
        mContents.load(R.drawable.restart_btn);
        mContents.load(R.drawable.x_close);
        mContents.load(R.drawable.dots_theme1);
        mContents.load(R.drawable.anim_spec_roweater);
        mContents.load(R.drawable.effect_roweater);
        mContents.load(R.drawable.skills);
        mContents.load(R.drawable.main_window_background);
    }
}
