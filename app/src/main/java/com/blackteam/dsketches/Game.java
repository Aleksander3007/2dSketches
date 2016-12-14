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

import java.util.ArrayList;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

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

    /**
     * Создание нового уровня.
     */
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

    /**
     * Использовать skill.
     * @param skillType Тип skill.
     */
    public void applySkill(Skill.Type skillType) {
        switch (skillType) {
            case RESHUFFLE:
                applySkillReshuffle();
                break;
            case FRIENDS:
                applySkillFriends();
                break;
            case CHASM:
                applySkillChasm();
                break;
        }
        player_.getSkill(skillType).use();
    }

    /**
     * Использовать Skill.Type.CHASM.
     */
    private void applySkillChasm() {
        CopyOnWriteArrayList<GameDot> dots = new CopyOnWriteArrayList<>();
        // Уничтожаем последние два ряда.
        for (int iRow = 0; iRow < 2; iRow++) {
            for (int iCol = 0; iCol < world_.getNumCols(); iCol++) {
                dots.add(world_.getDot(iRow, iCol));
            }
        }
        world_.deleteDots(dots);
    }

    /**
     * Использовать Skill.Type.FRIENDS.
     */
    private void applySkillFriends() {
        Log.i("World", "FRIENDS skill.");
        ArrayList<Integer> randomDotNumbers = new ArrayList<>();
        // У трёх разные случайных dots.
        int iFriend = 0;
        while (iFriend < 3) {
            int randomDotNo = (int) (Math.random() * (world_.getNumRows() * world_.getNumCols() - 1));
            boolean isFriend = false;
            // Должны быть три разных.
            for (int existRandomDotNo : randomDotNumbers) {
                if (existRandomDotNo == randomDotNo) {
                    isFriend = true;
                    break;
                }
            }

            if (!isFriend) {
                int randomRow = randomDotNo / world_.getNumCols();
                int randomCol = randomDotNo % world_.getNumCols();
                // Должны быть не универсальными.
                if (world_.getDot(randomRow, randomCol).getType() != GameDot.Types.UNIVERSAL) {
                    world_.createDot(GameDot.Types.UNIVERSAL,
                            world_.getDot(randomRow, randomCol).getSpecType(),
                            randomRow, randomCol
                    );
                    iFriend++;
                }
            }
        }
    }

    /**
     * Использовать Skill.Type.RESHUFFLE.
     */
    private void applySkillReshuffle() {
        Log.i("World", "RESHUFFLE skill.");
        for (int iRow = 0; iRow < world_.getNumRows(); iRow++) {
            for (int iCol = 0; iCol < world_.getNumCols(); iCol++) {
                int randomRow = (int) (Math.random() * world_.getNumRows());
                int randomCol = (int) (Math.random() * world_.getNumCols());

                GameDot.Types tempOrbType = world_.getDot(iRow, iCol).getType();
                GameDot.SpecTypes tempOrbSpecType = world_.getDot(iRow, iCol).getSpecType();

                world_.createDot(world_.getDot(randomRow, randomCol).getType(),
                        world_.getDot(randomRow, randomCol).getSpecType(),
                        iRow, iCol
                );

                world_.createDot(tempOrbType,
                        tempOrbSpecType,
                        randomRow, randomCol
                );
            }
        }
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
