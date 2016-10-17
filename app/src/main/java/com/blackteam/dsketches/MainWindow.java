package com.blackteam.dsketches;

import android.content.Context;
import android.util.Log;

import com.blackteam.dsketches.gui.ProfitLabel;
import com.blackteam.dsketches.gui.RestartButton;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.gui.StaticText;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.utils.NumberLabel;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

public class MainWindow implements Loadable {
    private World world_;
    private Player player_;
    private MenuWindow menuWindow_;

    private NumberLabel scoreLabel_;
    private RestartButton menuButton_;
    private SkillsPanel skillsPanel_;
    private ProfitLabel profitLabel_;
    private StaticText versionLabel_;

    private float width_;
    private float height_;

    private float screenPart_;

    public MainWindow(World world, Player player) {
        this.world_ = world;
        this.player_ = player;
    }

    public void init(final float screenWidth, final float screenHeight) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;

        setSize(screenWidth, screenHeight);

        restartLevel();
    }

    public void loadContent(ContentManager contents) {
        skillsPanel_ = new SkillsPanel(contents);
        scoreLabel_ = new NumberLabel(contents.get(R.drawable.numbers));
        menuButton_ = new RestartButton(contents.get(R.drawable.menu_btn));
        profitLabel_ = new ProfitLabel(contents.get(R.drawable.profit_numbers));
    }

    public void setMenu(MenuWindow menuWindow) {
        this.menuWindow_ = menuWindow;
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
            menuWindow_.show();
        }
        else if (skillsPanel_.hit(worldCoords)) {
            skillsPanel_.applySelectedSkill(world_);
        }
        else {
            world_.update();
            int profit = world_.getProfitByOrbs();
            if (profit > 0) {
                profitLabel_.setProfit(profit, new Vector2(worldCoords));
                player_.addScore(profit);
                scoreLabel_.setValue(player_.getScore());
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
        reset();
    }

    public void reset() {
        scoreLabel_.setValue(0);
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
                Main.VERSION,
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
}
