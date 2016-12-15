package com.blackteam.dsketches;

import android.util.Log;

import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.ProfitLabel;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.ArrayList;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {
    private World mWorld;
    private Player mPlayer;

    private ProfitLabel mProfitLabel;

    private float mWidth;
    private float mHeight;

    public Game(final Player player, SketchesManager sketchesManager, final ContentManager contents) {
        this.mPlayer = player;
        mWorld = new World(contents, sketchesManager);
        mProfitLabel = new ProfitLabel(contents.get(R.drawable.numbers));
    }

    public void resize(final float screenWidth, final float screenHeight) {
        this.mWidth = screenWidth;
        this.mHeight = screenHeight;
        setSize();
    }

    public void render(Graphics graphics) {
        mWorld.draw(graphics);
        mProfitLabel.render(graphics);
    }

    public boolean hit(Vector2 worldCoords) {
        return mWorld.hit(worldCoords);
    }

    public void touchUp(Vector2 worldCoords) {
        Log.i("Game", "touchUpHandle begin");
        mWorld.update();
        int profit = mWorld.getProfitByDots();
        if (profit > 0) {
            mProfitLabel.setProfit(profit, new Vector2(worldCoords));
            mPlayer.addScore(profit);
            mWorld.deleteSelectedDots();
            mWorld.removeSelection();
        }
        else {
            mWorld.removeSelection();
        }
    }

    public void restartLevel() {
        createLevel();
    }

    /**
     * Создание нового уровня.
     */
    public void createLevel() {
        mWorld.createLevel();
    }

    public void loadLevel() {
        // Если есть данные о расположении игровых точек.
        if (mPlayer.getGameDots() != null)
            mWorld.createLevel(mPlayer.getGameDots());
        else
            createLevel();
    }

    public void addObserver(Observer observer) {
        mWorld.addObserver(observer);
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
        mPlayer.getSkill(skillType).use();
    }

    /**
     * Использовать Skill.Type.CHASM.
     */
    private void applySkillChasm() {
        CopyOnWriteArrayList<GameDot> dots = new CopyOnWriteArrayList<>();
        // Уничтожаем последние два ряда.
        for (int iRow = 0; iRow < 2; iRow++) {
            for (int iCol = 0; iCol < mWorld.getNumCols(); iCol++) {
                dots.add(mWorld.getDot(iRow, iCol));
            }
        }
        mWorld.deleteDots(dots);
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
            int randomDotNo = (int) (Math.random() * (mWorld.getNumRows() * mWorld.getNumCols() - 1));
            boolean isFriend = false;
            // Должны быть три разных.
            for (int existRandomDotNo : randomDotNumbers) {
                if (existRandomDotNo == randomDotNo) {
                    isFriend = true;
                    break;
                }
            }

            if (!isFriend) {
                int randomRow = randomDotNo / mWorld.getNumCols();
                int randomCol = randomDotNo % mWorld.getNumCols();
                // Должны быть не универсальными.
                if (mWorld.getDot(randomRow, randomCol).getType() != GameDot.Types.UNIVERSAL) {
                    mWorld.createDot(GameDot.Types.UNIVERSAL,
                            mWorld.getDot(randomRow, randomCol).getSpecType(),
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
        for (int iRow = 0; iRow < mWorld.getNumRows(); iRow++) {
            for (int iCol = 0; iCol < mWorld.getNumCols(); iCol++) {
                int randomRow = (int) (Math.random() * mWorld.getNumRows());
                int randomCol = (int) (Math.random() * mWorld.getNumCols());

                GameDot.Types tempOrbType = mWorld.getDot(iRow, iCol).getType();
                GameDot.SpecTypes tempOrbSpecType = mWorld.getDot(iRow, iCol).getSpecType();

                mWorld.createDot(mWorld.getDot(randomRow, randomCol).getType(),
                        mWorld.getDot(randomRow, randomCol).getSpecType(),
                        iRow, iCol
                );

                mWorld.createDot(tempOrbType,
                        tempOrbSpecType,
                        randomRow, randomCol
                );
            }
        }
    }

    private void setSize() {
        Vector2 worldOffset = new Vector2(0f, this.mHeight / 25f);
        Size2 worldSize = new Size2(
                mWidth - worldOffset.x,
                mHeight - 2 * worldOffset.y
        );
        mWorld.init(worldOffset, worldSize);

        mProfitLabel.init(new Vector2(0, 0),
                new Size2(this.mHeight / 10f, this.mHeight / 10f));
    }
}
