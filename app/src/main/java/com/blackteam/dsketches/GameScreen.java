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

    public void init(final float screenWidth, final float screenHeight) {
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

        world_ = new World(worldOffset, worldSize);
        scoreLabel_ = new ScoreLabel(0, scoreLabelOffset, scoreLabelSize);
    }

    public void init() {
        world_.init();
        scoreLabel_.init();
    }

    public void onDraw(float[] mvpMatrix, final ShaderProgram shader) {
        scoreLabel_.draw(mvpMatrix, shader);
        world_.onDraw(mvpMatrix, shader);
    }

    public void loadContent(Context context) {
        scoreLabel_.loadContent(context);
        world_.loadContent(context);
    }

    public boolean hit(Vector2 worldCoords) {
        return world_.hit(worldCoords);
    }

    public void touchUp(Vector2 worldCoords) {
//        if (restartBtn_.hit(worldCoords)) {
//            Gdx.app.log("GameScreen", "hit restartBtn_");
//            world_.createLevel();
//            scoreLabel_.setScore(0);
//        } else {
            world_.update();
            int profit = world_.getProfitByOrbs();
            if (profit > 0) {
                scoreLabel_.addScore(profit);
                world_.deleteSelectedOrbs();
                world_.removeSelection();
            }
            else {
                world_.removeSelection();
            }
//        }
    }
}
