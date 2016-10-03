package com.blackteam.dsketches;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * По сути контроллер.
 */
public class Game implements View.OnTouchListener {
    private GameScreen screen_;

    public Game(GameScreen screen) {
        this.screen_ = screen;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case (MotionEvent.ACTION_UP):
                    if (BuildConfig.DEBUG) {
                        Log.i("Game.onTouch()", "Action was UP");
                        Log.i("Game.onTouch().x", String.valueOf(event.getX() * GameRenderer.uppX));
                        Log.i("Game.onTouch().y", String.valueOf(event.getY() * GameRenderer.uppY));
                    }
                    // TODO: По идеи везде hit и необходимо передавать Action.
                    screen_.touchUp(getWorldCoords(event.getX(),event.getY()));
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    screen_.hit(getWorldCoords(event.getX(), event.getY()));
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
