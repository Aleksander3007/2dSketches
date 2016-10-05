package com.blackteam.dsketches;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Отрисовка мира, а также обработка событий пользователя.
 */
public class GameView extends GLSurfaceView {
    private GameRenderer gameRenderer_;
    private GameScreen gameScreen_;

    public GameView(Context context) {
        super(context);

        gameScreen_ = new GameScreen();
        gameRenderer_ = new GameRenderer(context, gameScreen_);

        setEGLContextClientVersion(2);
        setRenderer(gameRenderer_);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case (MotionEvent.ACTION_UP):
                    if (BuildConfig.DEBUG) {
                        Log.i("GameController", "Action was UP");
                        Log.i("GameController.x", String.valueOf(event.getX() * GameRenderer.uppX));
                        Log.i("GameController.y", String.valueOf(event.getY() * GameRenderer.uppY));
                    }
                    // TODO: По идеи везде hit и необходимо передавать Action.
                    gameScreen_.touchUp(getWorldCoords(event.getX(),event.getY()));
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    gameScreen_.hit(getWorldCoords(event.getX(), event.getY()));
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
