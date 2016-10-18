package com.blackteam.dsketches;

import android.app.Activity;
import android.os.Bundle;

import com.blackteam.dsketches.utils.ExceptionHandler;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Main extends Activity {
    public static String VERSION;

    private GameView gameView_;

    private boolean rendererSet = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        VERSION = getApplicationContext().getResources().getString(R.string.version_str);

        try {
            gameView_ = new GameView(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

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
