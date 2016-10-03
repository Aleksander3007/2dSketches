package com.blackteam.dsketches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.SizeF;

public class GameScreen {

    private World world_;
    private ScoreLabel scoreLabel_;
    private RestartButton restartBtn_;
    private Texture restartBtnTexture_;
    //private SkillsPanel skillsPanel;

    private float width_;
    private float height_;

    public void init(final Context context, final float screenWidth, final float screenHeight) {
        world_ = new World(context);
        scoreLabel_ = new ScoreLabel(context);
        restartBtn_ = new RestartButton(new Texture(context, R.drawable.restart_btn));
        setSize(screenWidth, screenHeight);
    }

    private void setSize(final float screenWidth, final float screenHeight) {
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

        Size2 restartBtnSize = new Size2(
                ((screenHeight - worldSize.height) * 2 / 3),
                ((screenHeight - worldSize.height) * 2 / 3)
        );
        Vector2 restartBtnOffset = new Vector2(
                width_ - restartBtnSize.width,
                height_ - restartBtnSize.height
        );

        world_.setSize(worldOffset, worldSize);
        scoreLabel_.setSize(0, scoreLabelOffset, scoreLabelSize);
        restartBtn_.init(restartBtnOffset, restartBtnSize);
    }

    public void init() {
        world_.init();
        scoreLabel_.init();
    }

    public void onDraw(float[] mvpMatrix, final ShaderProgram shader) {
        scoreLabel_.draw(mvpMatrix, shader);
        world_.onDraw(mvpMatrix, shader);
        restartBtn_.draw(mvpMatrix, shader);
    }

    public boolean hit(Vector2 worldCoords) {
        return world_.hit(worldCoords);
    }

    public void touchUp(Vector2 worldCoords) {
        Log.i("GameScreen", "touchUp begin");
        if (restartBtn_.hit(worldCoords)) {
            Log.i("GameScreen", "restartBtn_.hit end");
            world_.createLevel();
            Log.i("GameScreen", "World.createLevel end");
            scoreLabel_.setScore(0);
            Log.i("GameScreen", "scoreLabel_.setScore end");
        } else {
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
        }
    }
}
