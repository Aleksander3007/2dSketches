package com.blackteam.dsketches;

import android.app.Activity;
import android.os.Bundle;
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
 * Created by Aleksander Ermakov on 23.09.2016.
 */
public class Main extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            // если хотим, чтобы приложение было полноэкранным
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // и без заголовка
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(new GameView(this));
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Internal error.", Toast.LENGTH_LONG).show();
        }
    }
}
