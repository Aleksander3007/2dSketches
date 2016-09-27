package com.blackteam.dsketches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.support.v4.util.ArrayMap;

import com.blackteam.tripledouble.BuildConfig;

public class GameView extends SurfaceView
{
    /** Размер игровой, относительно него строится все размеры объектов. */
    private static int gameWidth_ = 480;
    private static int gameHeight_ = 640;

    private GameThread gameThread_;
    /** Переменная запускающая поток рисования */
    public static boolean running_ = false;

    private GameScreen gameScreen_;
    private ArrayMap<String, Bitmap> bitmaps_ = new ArrayMap<String, Bitmap>();

    private float screenFactor_;


    //-------------Start of GameThread--------------------------------------------------\\
    public class GameThread extends Thread
    {
        private GameView view;

        public GameThread(GameView view)
        {
            this.view = view;
        }

        /**Задание состояния потока*/
        public void setRunning(boolean run)
        {
            running_ = run;
        }

        /** Действия, выполняемые в потоке */
        @Override
        public void run()
        {
            while (running_)
            {
                Canvas canvas = null;
                try
                {
                    // подготовка Canvas-а.
                    canvas = view.getHolder().lockCanvas();
                    synchronized (view.getHolder())
                    {
                        // собственно рисование.
                        onDraw(canvas);
                    }
                }
                catch (Exception e) { }
                finally
                {
                    if (canvas != null)
                    {
                        view.getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
    //-------------End of GameThread--------------------------------------------------\\

    public GameView(Context context)
    {
        super(context);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        screenFactor_ = (float)(screenWidth) / (float)(gameWidth_);
        gameHeight_ = (int)((float)(screenHeight) / ((float)(screenWidth) / (float)(gameWidth_)));

        gameScreen_ = new GameScreen(gameWidth_, gameHeight_, bitmaps_);
        loadContent();
        gameScreen_.init();

        this.setOnTouchListener(new Game(gameScreen_, screenFactor_));

        gameThread_ = new GameThread(this);

        /*Рисуем все наши объекты*/
        getHolder().addCallback(new SurfaceHolder.Callback()
        {
            /*** Уничтожение области рисования */
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                boolean retry = true;
                gameThread_.setRunning(false);
                while (retry)
                {
                    try
                    {
                        // ожидание завершение потока
                        gameThread_.join();
                        retry = false;
                    }
                    catch (InterruptedException e) { }
                }
            }

            /** Создание области рисования */
            public void surfaceCreated(SurfaceHolder holder)
            {
                //gameThread_.setRunning(true);
                //gameThread_.start();
                Canvas canvas = getHolder().lockCanvas();
                synchronized (getHolder())
                {
                    // собственно рисование.
                    onDraw(canvas);
                }
                getHolder().unlockCanvasAndPost(canvas);
            }

            /** Изменение области рисования */
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {
            }
        });
    }

    public float getScreenFactor() {
        return screenFactor_;
    }

    private void loadContent() {
        gameScreen_.loadContent(this);
        if (BuildConfig.DEBUG) { Log.i("GameView", "Content is loaded."); }
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.scale(screenFactor_, screenFactor_);
        gameScreen_.onDraw(canvas);
    }
}
