package com.blackteam.dsketches;

import android.util.Log;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.GameImage;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.ProfitLabel;
import com.blackteam.dsketches.gui.StaticText;
import com.blackteam.dsketches.gui.NumberLabel;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.Observer;

public class Game {
    private World world_;
    private Player player_;

    private NumberLabel scoreLabel_;
    private ProfitLabel profitLabel_;
    private StaticText versionLabel_;

    private float width_;
    private float height_;

    private float screenPart_;

    public Game(final Player player, SketchesManager sketchesManager, final ContentManager contents) {
        this.player_ = player;
        world_ = new World(contents, sketchesManager);
        scoreLabel_ = new NumberLabel(contents.get(R.drawable.numbers));
        profitLabel_ = new ProfitLabel(contents.get(R.drawable.numbers));

        scoreLabel_.setValue(player_.getScore());
    }

    public void resize(final float screenWidth, final float screenHeight) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;
        setSize();
    }

    public void render(Graphics graphics) {
        if (scoreLabel_ == null)
            Log.i("Game", "render scoreLabel_ == null");

        //background_.draw(graphics);
        scoreLabel_.render(graphics);
        world_.draw(graphics);
        profitLabel_.render(graphics);
        versionLabel_.draw(graphics);
    }

    public boolean hit(Vector2 worldCoords) {
        return world_.hit(worldCoords);
    }

    public void touchUp(Vector2 worldCoords) {
        Log.i("Game", "touchUpHandle begin");
        world_.update();
        int profit = world_.getProfitByDots();
        if (profit > 0) {
            profitLabel_.setProfit(profit, new Vector2(worldCoords));
            player_.addScore(profit);
            scoreLabel_.setValue(player_.getScore());
            world_.deleteSelectedDots();
            world_.removeSelection();
        }
        else {
            world_.removeSelection();
        }
    }

    public void restartLevel() {
        createLevel();
        reset();
    }

    public void createLevel() {
        world_.createLevel();
    }

    public void loadLevel() {
        // Если есть данные о расположении игровых точек.
        if (player_.getGameDots() != null)
            world_.createLevel(player_.getGameDots());
        else
            createLevel();
    }

    public void reset() {
        scoreLabel_.setValue(0);
    }

    public void addObserver(Observer observer) {
        world_.addObserver(observer);
    }

    private void setSize() {

        screenPart_ = this.height_ / (3 + 15 + 1);

        Vector2 worldOffset = new Vector2(0, screenPart_);

        Size2 worldSize = new Size2(
                width_ - worldOffset.x,
                (screenPart_ * 15)
        );

        Vector2 scoreLabelOffset = new Vector2(0,
                (worldOffset.y + worldSize.height) + screenPart_);
        Size2 scoreLabelSize = new Size2(
                width_ - scoreLabelOffset.x,
                screenPart_);

        versionLabel_ = new StaticText(
                MainActivity.VERSION,
                new Vector2(width_ / 2,  height_ - screenPart_),
                new Size2(width_ / 4, screenPart_)
        );

        world_.init(worldOffset, worldSize);
        scoreLabel_.init(scoreLabelOffset, scoreLabelSize);
        profitLabel_.init(new Vector2(0, 0),
                new Size2(1.5f * screenPart_, 1.5f * screenPart_));
    }
}
