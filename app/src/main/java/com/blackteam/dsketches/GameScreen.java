package com.blackteam.dsketches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.util.ArrayMap;
import android.util.SizeF;

public class GameScreen {

    private World world_;
    private ScoreLabel scoreLabel_;
    private RestartButton restartBtn_;
    //private SkillsPanel skillsPanel;

    private float width_;
    private float height_;

    private Orb orb_;

    public GameScreen(final float screenWidth, final float screenHeight, ShaderProgram shader) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;
        Vector2 worldOffset = new Vector2(0, 0);
        Size2 worldSize = new Size2(
                width_ - worldOffset.x,
                height_ - worldOffset.y - (screenHeight / 7));

        world_ = new World(worldOffset, worldSize, shader);

        scoreLabel_ = new ScoreLabel();
    }

    public void init() {
        world_.init();
    }

    public void onDraw(float[] mvpMatrix) {
        world_.onDraw(mvpMatrix);
    }

    public void loadContent(Context context) {
        world_.loadContent(context);
    }

    public boolean hit(Vector2 worldCoords) {
        if (world_.hit(worldCoords)) {
            return true;
        }
        return false;
    }
}
