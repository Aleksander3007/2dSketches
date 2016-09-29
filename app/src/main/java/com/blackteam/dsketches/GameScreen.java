package com.blackteam.dsketches;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.util.ArrayMap;

public class GameScreen {

    private World world_;
    private ScoreLabel scoreLabel_;
    private RestartButton restartBtn_;
    //private SkillsPanel skillsPanel;

    private float width_;
    private float height_;

    private Orb orb_;

    public GameScreen(final float screenWidth, final float screenHeight) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;
        Vector2 worldOffset = new Vector2(0, 0);
        world_ = new World(
                worldOffset,
                (int)(width_ + worldOffset.x), (int)(height_ - worldOffset.y)
        );
    }

    public void init() {
        world_.init();
    }

    public void onDraw(Canvas canvas) {
        world_.onDraw(canvas);
    }

    public void loadContent(GameView gameView) {
        world_.loadContent(gameView);
    }

    public boolean hit(Vector2 worldCoords) {
        if (world_.hit(worldCoords)) {
            return true;
        }
        return false;
    }
}
