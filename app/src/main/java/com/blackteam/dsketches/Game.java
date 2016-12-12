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

    private ProfitLabel profitLabel_;

    private float width_;
    private float height_;

    public Game(final Player player, SketchesManager sketchesManager, final ContentManager contents) {
        this.player_ = player;
        world_ = new World(contents, sketchesManager);
        profitLabel_ = new ProfitLabel(contents.get(R.drawable.numbers));
    }

    public void resize(final float screenWidth, final float screenHeight) {
        this.width_ = screenWidth;
        this.height_ = screenHeight;
        setSize();
    }

    public void render(Graphics graphics) {
        world_.draw(graphics);
        profitLabel_.render(graphics);
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
            world_.deleteSelectedDots();
            world_.removeSelection();
        }
        else {
            world_.removeSelection();
        }
    }

    public void restartLevel() {
        createLevel();
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

    public void addObserver(Observer observer) {
        world_.addObserver(observer);
    }

    private void setSize() {
        Vector2 worldOffset = new Vector2(0f, this.height_ / 25f);
        Size2 worldSize = new Size2(
                width_ - worldOffset.x,
                height_ - 2 * worldOffset.y
        );
        world_.init(worldOffset, worldSize);

        profitLabel_.init(new Vector2(0, 0),
                new Size2(this.height_ / 10f, this.height_ / 10f));
    }
}
