package com.blackteam.dsketches;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blackteam.dsketches.utils.ExceptionHandler;

public class MainActivity extends Activity {
    public static String VERSION;

    private GameView gameView_;

    private boolean rendererSet = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        VERSION = getApplicationContext().getResources().getString(R.string.version_str);

        setContentView(R.layout.main);
        gameView_ = (GameView) findViewById(R.id.gameview);

        rendererSet = true;
    }

    public void menuOpenOnClick(View view) {
        Intent intent = new Intent(getBaseContext(), MainMenuActivity.class);
        startActivity(intent);
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
