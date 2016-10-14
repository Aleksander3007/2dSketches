package com.blackteam.dsketches;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AchievementsManager implements Observer{
    @Override
    public void update(Observable observable, Object data) {
        final Sketch.Types sketchType = (Sketch.Types) data;

        // TODO: Ачивки могут быть не только за sketch, но еще например за набранное
        // количество очков в сумме, за раз, и т.д.

        // TODO: Необходимо проверять есть ли уже такая ачивка.
        // TODO: Отобразить достижение, если получил ачивку.
        // TODO: Добавить, что ачивка уже есть.
        switch (sketchType) {
            case ROW_3:
                Log.i("Achievement", "ROW_3");
                break;
            case ROW_5:
                Log.i("Achievement", "ROW_5");
                break;
            default:
                // Ничего не делаем в этом случае.
                break;
        }
    }
}
