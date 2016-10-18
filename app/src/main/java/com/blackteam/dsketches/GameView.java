package com.blackteam.dsketches;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.blackteam.dsketches.utils.Vector2;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;

/**
 * Отрисовка мира, а также обработка событий пользователя.
 */
public class GameView extends GLSurfaceView {
    private GameRenderer gameRenderer_;
    private MainWindow mainWindow_;
    private MenuWindow menuWindow_;

    private Player player_;
    private World world_;
    private AchievementsManager achievementsManager_;

    private ArrayList<Loadable> loadableObjects_ = new ArrayList<>();

    public GameView(Context context) throws IOException, XmlPullParserException {
        super(context);

        player_ = new Player();
        world_ = new World();
        loadableObjects_.add(world_);

        achievementsManager_ = new AchievementsManager();
        achievementsManager_.loadContent(context);
        world_.addObserver(achievementsManager_);

        Log.d("GameView",  "Models are created.");

        mainWindow_ = new MainWindow(world_, player_);
        menuWindow_ = new MenuWindow(world_, mainWindow_);
        mainWindow_.setMenu(menuWindow_);
        loadableObjects_.add(mainWindow_);
        loadableObjects_.add(menuWindow_);
        Log.d("GameView",  "Windows are created.");

        gameRenderer_ = new GameRenderer(context, mainWindow_, menuWindow_, loadableObjects_);

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
