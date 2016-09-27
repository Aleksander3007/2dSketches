package com.blackteam.dsketches;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.util.ArrayMap;

public class GameScreen {

    private World world_;
    private ScoreLabel scoreLabel_;
    private RestartButton restartBtn_;
    //private SkillsPanel skillsPanel;

    private int width_;
    private int height_;

    private Orb orb_;

    public GameScreen(final int screenWidth, final int screenHeight, ArrayMap<String, Bitmap> bitmaps) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;
        Vector2 worldOffset = new Vector2(0, 80);
        world_ = new World(
                worldOffset,
                (int)(width_ + worldOffset.x), (int)(height_ - worldOffset.y),
                bitmaps
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
