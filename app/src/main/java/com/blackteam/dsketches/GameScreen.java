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

        Vector2 scoreLabelOffset = new Vector2(0,
                (worldOffset.y + worldSize.height) + (screenHeight - worldSize.height) / 3);
        Size2 scoreLabelSize = new Size2(
                width_ - scoreLabelOffset.x,
                (screenHeight - worldSize.height) / 3);

        world_ = new World(worldOffset, worldSize, shader);
        scoreLabel_ = new ScoreLabel(0, scoreLabelOffset, scoreLabelSize, shader);
    }

    public void init() {
        world_.init();
        scoreLabel_.init();
    }

    public void onDraw(float[] mvpMatrix) {
        world_.onDraw(mvpMatrix);
        scoreLabel_.draw(mvpMatrix);
    }

    public void loadContent(Context context) {
        world_.loadContent(context);
        scoreLabel_.loadContent(context);
    }

    public boolean hit(Vector2 worldCoords) {
        return world_.hit(worldCoords);
    }
}
