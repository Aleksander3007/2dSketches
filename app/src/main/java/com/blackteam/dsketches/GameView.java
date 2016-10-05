package com.blackteam.dsketches;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Отрисовка мира, а также обработка событий пользователя.
 */
public class GameView extends GLSurfaceView {
    private GameRenderer gameRenderer_;
    private MainWindow mainWindow_;
    private MenuWindow menuWindow_;
    private GameController gameController_;

    public GameView(Context context) {
        super(context);

        gameController_ = new GameController();

        mainWindow_ = new MainWindow(gameController_);
        menuWindow_ = new MenuWindow(gameController_);

        gameController_.setViews(mainWindow_, menuWindow_);

        gameRenderer_ = new GameRenderer(context, mainWindow_, menuWindow_);

        setEGLContextClientVersion(2);
        setRenderer(gameRenderer_);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                // TODO: Отдельный метод для каждого Events.
                case (MotionEvent.ACTION_UP):
                    if (BuildConfig.DEBUG) {
                        Log.i("GameView", "Action was UP");
                        Log.i("GameView.x", String.valueOf(event.getX() * GameRenderer.uppX));
                        Log.i("GameView.y", String.valueOf(event.getY() * GameRenderer.uppY));
                    }
                    if (menuWindow_.isVisible()) {
                        menuWindow_.touchUp(getWorldCoords(event.getX(),event.getY()));
                        return true;
                    }
                    // TODO: По идеи везде hit и необходимо передавать Action.
                    mainWindow_.touchUp(getWorldCoords(event.getX(),event.getY()));

                    return true;
                case (MotionEvent.ACTION_MOVE):
                    mainWindow_.hit(getWorldCoords(event.getX(), event.getY()));
                    return true;
                default:
                    return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Vector2 getWorldCoords(float screenX, float screenY) {
        return new Vector2(
                screenX * GameRenderer.uppX,
                (GameRenderer.height - screenY) * GameRenderer.uppY
        );
    }
}
