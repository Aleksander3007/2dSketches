package com.blackteam.dsketches;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.blackteam.dsketches.utils.Vector2;

/**
 * Отрисовка мира, а также обработка событий пользователя.
 */
public class GameView extends GLSurfaceView {
    private GameRenderer gameRenderer_;
    private ContentManager contents_;
    private MainWindow mainWindow_;
    private MenuWindow menuWindow_;

    private Player player_;
    private World world_;
    private AchievementsManager achievementsManager_;

    public GameView(Context context) {
        super(context);

        contents_ = new ContentManager(context);

        player_ = new Player();
        world_ = new World();

        achievementsManager_ = new AchievementsManager();
        world_.addObserver(achievementsManager_);

        Log.i("GameView",  "Models are created.");

        mainWindow_ = new MainWindow(world_, player_, menuWindow_);
        menuWindow_ = new MenuWindow(world_, mainWindow_);

        Log.i("GameView",  "Windows are created.");

        gameRenderer_ = new GameRenderer(context, contents_, mainWindow_, menuWindow_, world_);

        setEGLContextClientVersion(2);
        setRenderer(gameRenderer_);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                // TODO: Отдельный метод для каждого Events.
                case (MotionEvent.ACTION_UP):
                    if (BuildConfig.DEBUG) {
                        Log.i("GameView", "Action was UP");
                    }
                    if (menuWindow_.isVisible()) {
                        menuWindow_.touchUp(getWorldCoords(event.getX(),event.getY()));
                        return true;
                    }
                    // TODO: По идеи везде hit и необходимо передавать Action.
                    mainWindow_.touchUp(getWorldCoords(event.getX(),event.getY()));

                    return true;
                case (MotionEvent.ACTION_MOVE):
                    mainWindow_.hit(getWorldCoords(event.getX(), event.getY()));
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
