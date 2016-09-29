package com.blackteam.dsketches;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Aleksander on 26.09.2016.
 */
public class Game implements View.OnTouchListener {
    private GameScreen screen_;
    private float screenFactor_;

    public Game(GameScreen screen, final float screenFactor) {
        this.screen_ = screen;
        this.screenFactor_ = screenFactor;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_UP) :
                if (BuildConfig.DEBUG) {
                    Log.i("Game.onTouch()", "Action was DOWN");
                    Log.i("Game.onTouch().x", String.valueOf(event.getX() / screenFactor_));
                    Log.i("Game.onTouch().y", String.valueOf(event.getY() / screenFactor_));
                }
                return true;
            case (MotionEvent.ACTION_MOVE) :
                screen_.hit(new Vector2(
                        event.getX() / screenFactor_,
                        event.getY() / screenFactor_));
                return true;
            default:
                return true;
        }
    }
}
