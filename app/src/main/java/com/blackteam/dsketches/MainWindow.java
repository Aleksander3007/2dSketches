package com.blackteam.dsketches;

import android.content.Context;
import android.util.Log;

import com.blackteam.dsketches.Utils.NumberLabel;
import com.blackteam.dsketches.Utils.Size2;
import com.blackteam.dsketches.Utils.Vector2;

public class MainWindow {
    public static String VERSION_;

    private GameController gameController_;

    private World world_;
    private NumberLabel scoreLabel_;
    private RestartButton menuButton_;
    private SkillsPanel skillsPanel_;
    private ProfitLabel profitLabel_;
    private StaticText versionLabel_;
    private AchievementsManager achievementsManager_;

    private float width_;
    private float height_;

    private float screenPart_;

    // TODO: Это должно быт не здесь.
    private int score_;

    public MainWindow(GameController gameController) {
        this.gameController_ = gameController;
    }

    public void init(final Context context, final float screenWidth, final float screenHeight) {
        VERSION_ = context.getResources().getString(R.string.version_str);

        this.width_ = screenWidth;
        this.height_ = screenHeight;

        Texture profitDigits = new Texture(context, R.drawable.profit_numbers);
        Texture scoreDigits = new Texture(context, R.drawable.numbers);

        world_ = new World(context);
        skillsPanel_ = new SkillsPanel(context);
        scoreLabel_ = new NumberLabel(scoreDigits);
        menuButton_ = new RestartButton(new Texture(context, R.drawable.menu_btn));
        profitLabel_ = new ProfitLabel(profitDigits);

        Log.i("MainWindow", "init");
        if (scoreLabel_ == null)
            Log.i("MainWindow", "init scoreLabel_ == null");

        achievementsManager_ = new AchievementsManager();
        world_.addObserver(achievementsManager_);

        setSize(screenWidth, screenHeight);

        restartLevel();
    }

    private void setSize(final float screenWidth, final float screenHeight) {
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

        versionLabel_ = new StaticText(
                VERSION_,
                new Vector2(width_ / 2,  height_ - screenPart_),
                new Size2(width_ / 4, screenPart_)
        );

        skillsPanel_.init(skillsPanelOffset, skillsPanelSize);
        world_.init(worldOffset, worldSize);
        scoreLabel_.init(scoreLabelSize);
        scoreLabel_.setPosition(scoreLabelOffset);
        menuButton_.init(restartBtnOffset, restartBtnSize);
        profitLabel_.init(new Size2(screenPart_, screenPart_));
    }

    // TODO: mvpMatrix, shader, elapsedTime в класс Graphics упаковать.
    public void render(float[] mvpMatrix, final ShaderProgram shader, float elapsedTime) {
        if (scoreLabel_ == null)
            Log.i("MainWindow", "render scoreLabel_ == null");

        scoreLabel_.render(mvpMatrix, shader);
        world_.draw(mvpMatrix, shader);
        menuButton_.draw(mvpMatrix, shader);
        skillsPanel_.draw(mvpMatrix, shader);
        profitLabel_.render(mvpMatrix, shader, elapsedTime);
        versionLabel_.draw(mvpMatrix, shader);
    }

    public boolean hit(Vector2 worldCoords) {
        return world_.hit(worldCoords);
    }

    public void touchUp(Vector2 worldCoords) {
        Log.i("MainWindow", "touchUp begin");
        if (menuButton_.hit(worldCoords)) {
            gameController_.openMenu();
        }
        else if (skillsPanel_.hit(worldCoords)) {
            skillsPanel_.applySelectedSkill(world_);
        }
        else {
            world_.update();
            int profit = world_.getProfitByOrbs();
            if (profit > 0) {
                profitLabel_.setProfit(profit, new Vector2(worldCoords));
                score_ += profit;
                scoreLabel_.setValue(score_);
                world_.deleteSelectedOrbs();
                world_.removeSelection();
            }
            else {
                world_.removeSelection();
            }
        }
    }

    public void restartLevel() {
        world_.createLevel();
        scoreLabel_.setValue(0);
    }
}
