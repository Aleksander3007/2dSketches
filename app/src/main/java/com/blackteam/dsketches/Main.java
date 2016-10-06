package com.blackteam.dsketches;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Мысли и развитие.
 * 1. Сделать ачивки - pattern Observer (Наблюдатель). - Собрать фигуру, собрать кол-во очков.
 * 1.1. Открывать новое за ачивки, очки (например, какие-нибудь новые  фоны, новые Orbs и т.д.)
 * 2. Контент
 * 3. Возможно нужен конечный автомат, как минимум два состояни - загрузка и игра.
 * 		Хотя если загрузку вынести (т.к. по сути один экран и весь контент загружается сразу).
 * 		То и состояний не будет.
 * 4. implements interface RenderingObject или class DisplayableObject.
 * 5. Выделение по диагонали?.
 * 6. Отмена действий?
 * 7. Может убрать прослойку DisplayableObject (она ничего полезного не делает или делает). Наследовать только от Spite.
 */

// TODO: Тема с формами (можно выделять формы (не только прямые линии из Orbs): лестница, жираф, ноль и т.д.).
// TODO: Ачивки (Собрать фигуру, собрать кол-во очков).
// TODO: Зал славы.
// TODO: Отдельное окно для просмотра ачивок.
// TODO: Анимация (на нажатие кнопки, на выделение объекта, отображение сколько очков за сет).
// TODO: Обдумать и создать все типы реальные (скорее всего должны быть разные формы для разного цвета - например, красный - сфера, зеленый - квандрат и т.д.).
// TODO: Описать остальные спец. orb.
// TODO: Реализовать оставшиеся Skills.
// TODO: Звуковое сопровождение действий.
// TODO: Игровой баланс.
// TODO: Кнопка exit.

public class Main extends Activity {
    private GameView gameView_;

    private boolean rendererSet = false;

    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            gameView_ = new GameView(this);
            rendererSet = true;
            setContentView(gameView_);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Internal error.", Toast.LENGTH_LONG).show();
        }
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
