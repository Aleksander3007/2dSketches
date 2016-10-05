package com.blackteam.dsketches;

import android.content.Context;
import android.util.Log;

public class GameScreen {

    private World world_;
    private ScoreLabel scoreLabel_;
    private RestartButton restartBtn_;
    private SkillsPanel skillsPanel_;
    private ProfitLabel profitLabel_;

    private float width_;
    private float height_;

    private float screenPart_;

    public void init(final Context context, final float screenWidth, final float screenHeight) {
        world_ = new World(context);
        skillsPanel_ = new SkillsPanel(context);
        scoreLabel_ = new ScoreLabel(context);
        restartBtn_ = new RestartButton(new Texture(context, R.drawable.restart_btn));
        profitLabel_ = new ProfitLabel(context);

        setSize(screenWidth, screenHeight);
    }

    private void setSize(final float screenWidth, final float screenHeight) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;

        screenPart_ = this.height_ / (3 + 15 + 3);

        Vector2 skillsPanelOffset = new Vector2(0, screenPart_);
        Size2 skillsPanelSize = new Size2(
                width_ - skillsPanelOffset.x,
                screenPart_
        );

        Vector2 worldOffset = new Vector2(0,
                skillsPanelOffset.y + skillsPanelSize.height + screenPart_
        );

        Size2 worldSize = new Size2(
                width_ - worldOffset.x,
                (screenPart_ * 15)
        );

        Vector2 scoreLabelOffset = new Vector2(0,
                (worldOffset.y + worldSize.height) + screenPart_);
        Size2 scoreLabelSize = new Size2(
                width_ - scoreLabelOffset.x,
                screenPart_);

        Size2 restartBtnSize = new Size2(
                2.0f * screenPart_,
                2.0f * screenPart_
        );
        Vector2 restartBtnOffset = new Vector2(
                width_ - restartBtnSize.width,
                height_ - restartBtnSize.height
        );

        skillsPanel_.init(skillsPanelOffset, skillsPanelSize);
        world_.init(worldOffset, worldSize);
        scoreLabel_.init(0, scoreLabelOffset, scoreLabelSize);
        restartBtn_.init(restartBtnOffset, restartBtnSize);
        profitLabel_.init(new Size2(screenPart_, screenPart_));

    }

    public void render(float[] mvpMatrix, final ShaderProgram shader, float elapsedTime) {
        scoreLabel_.draw(mvpMatrix, shader);
        world_.draw(mvpMatrix, shader);
        restartBtn_.draw(mvpMatrix, shader);
        skillsPanel_.draw(mvpMatrix, shader);
        profitLabel_.render(mvpMatrix, shader, elapsedTime);
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
                profitLabel_.setScore(profit, new Vector2(width_ / 2, height_ / 2));
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
