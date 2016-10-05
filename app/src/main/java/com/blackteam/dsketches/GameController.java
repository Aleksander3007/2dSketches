package com.blackteam.dsketches;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameController {

    private MainWindow mainWindow_;
    private MenuWindow menuWindow_;

    public void setViews(MainWindow mainWindow, MenuWindow menuWindow) {
        this.mainWindow_ = mainWindow;
        this.menuWindow_ = menuWindow;
    }

    public void openMenu() {
        menuWindow_.setVisible();
    }

    public void restartLevel() {
        mainWindow_.restartLevel();
        menuWindow_.setInvisible();
    }
}
