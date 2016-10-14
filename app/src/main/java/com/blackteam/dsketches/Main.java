package com.blackteam.dsketches;

import android.app.Activity;
import android.os.Bundle;

import com.blackteam.dsketches.utils.ExceptionHandler;

public class Main extends Activity {
    private GameView gameView_;

    private boolean rendererSet = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        gameView_ = new GameView(this);
        rendererSet = true;
        setContentView(gameView_);
    }

    @Override
    protected void onPause() { super.onPause();
        if (rendererSet) {
            gameView_.onPause();
        }
    }

    @Override
    protected void onResume() { super.onResume();
        if (rendererSet) {
            gameView_.onResume();
        }
    }

}
