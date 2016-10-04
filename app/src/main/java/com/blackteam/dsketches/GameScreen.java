package com.blackteam.dsketches;

import android.content.Context;
import android.util.Log;

public class GameScreen {

    private World world_;
    private ScoreLabel scoreLabel_;
    private RestartButton restartBtn_;
    private Texture restartBtnTexture_;
    private SkillsPanel skillsPanel_;

    private float width_;
    private float height_;

    public void init(final Context context, final float screenWidth, final float screenHeight) {
        world_ = new World(context);
        skillsPanel_ = new SkillsPanel(context);
        scoreLabel_ = new ScoreLabel(context);
        restartBtn_ = new RestartButton(new Texture(context, R.drawable.restart_btn));
        setSize(screenWidth, screenHeight);
    }

    private void setSize(final float screenWidth, final float screenHeight) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;

        float screenPart = this.height_ / (3 + 15 + 3);

        Vector2 skillsPanelOffset = new Vector2(0, screenPart);
        Size2 skillsPanelSize = new Size2(
                width_ - skillsPanelOffset.x,
                screenPart
        );

        Vector2 worldOffset = new Vector2(0,
                skillsPanelOffset.y + skillsPanelSize.height + screenPart
        );

        Size2 worldSize = new Size2(
                width_ - worldOffset.x,
                (screenPart * 15)
        );

        Vector2 scoreLabelOffset = new Vector2(0,
                (worldOffset.y + worldSize.height) + screenPart);
        Size2 scoreLabelSize = new Size2(
                width_ - scoreLabelOffset.x,
                screenPart);

        Size2 restartBtnSize = new Size2(
                2.0f * screenPart,
                2.0f * screenPart
        );
        Vector2 restartBtnOffset = new Vector2(
                width_ - restartBtnSize.width,
                height_ - restartBtnSize.height
        );

        skillsPanel_.init(skillsPanelOffset, skillsPanelSize);
        world_.init(worldOffset, worldSize);
        scoreLabel_.init(0, scoreLabelOffset, scoreLabelSize);
        restartBtn_.init(restartBtnOffset, restartBtnSize);
    }

    public void onDraw(float[] mvpMatrix, final ShaderProgram shader) {
        scoreLabel_.draw(mvpMatrix, shader);
        world_.draw(mvpMatrix, shader);
        restartBtn_.draw(mvpMatrix, shader);
        skillsPanel_.draw(mvpMatrix, shader);
    }

    public boolean hit(Vector2 worldCoords) {
        return world_.hit(worldCoords);
    }

    public void touchUp(Vector2 worldCoords) {
        Log.i("GameScreen", "touchUp begin");
        if (restartBtn_.hit(worldCoords)) {
            world_.createLevel();
            scoreLabel_.setScore(0);
        }
        if (skillsPanel_.hit(worldCoords)) {
            skillsPanel_.applySelectedSkill(world_);
        }
        else {
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
