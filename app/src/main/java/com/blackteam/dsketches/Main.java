package com.blackteam.dsketches;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Мысли и развитие.
 * 1. Сделать ачивки - pattern Observer (Наблюдатель).
 * 1.1. Открывать новое за очки (например, какие-нибудь новые  фоны, новые Orbs и т.д.)
 * 2. Контент
 * 3.  3d - прозрачные orbs (надо ли?). - избыточно получается!
 * 4. Кнопка рестарта.
 * 5. Возможно нужен конечный автомат, как минимум два состояни - загрузка и игра.
 * 		Хотя если загрузку вынести (т.к. по сути один экран и весь контент загружается сразу).
 * 		То и состояний не будет.
 * 6. implements interface RenderingObject или class DisplayableObject.
 * 7. Выделение по диагонали?.
 * 8. Отмена действий?
 */

// TODO: OpenGL.
// TODO: Переименовать проект на 2dSketch.
// TODO: Панель SkillsPanel. "Перемешать"("Перетасовка"), "Обратить три случайных в соседний цвет"("Хороший сосед"), "Убрать два нижних ряда"("Пропасть")
// TODO: Тема с формами.
// TODO: Ачивки.
// TODO: Анимация (на нажатие кнопки, на выделение объекта, отображение сколько очков за сет).
// TODO: Звуковое сопровождение действий.
// TODO: Обдумать и создать все типы реальные (скорее всего должны быть разные формы для разного цвета - например, красный - сфера, зеленый - квандрат и т.д.).
// TODO: Описать остальные спец. orb.
// TODO: Игровой баланс.
// TODO: Кнопка exit.

/**
 * Главный класс.
 */
public class Main extends Activity {
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    public void onCreate(Bundle savedInstanceState) {
        try {
            Log.i("Version", "0.0.0.19");
            super.onCreate(savedInstanceState);

            glSurfaceView = new GLSurfaceView(this);

            // Проверяем поддерживается ли OpenGL ES 2.0.
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
            if (supportsEs2) {
                glSurfaceView.setEGLContextClientVersion(2);
                glSurfaceView.setRenderer(new GameRenderer(this));
                rendererSet = true;
            } else {
                Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
                return;
            }

            setContentView(glSurfaceView);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Internal error.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() { super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() { super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

}
