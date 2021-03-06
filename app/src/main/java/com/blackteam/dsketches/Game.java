package com.blackteam.dsketches;

import android.util.Log;

import com.blackteam.dsketches.models.gamedots.GameDot;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.ProfitLabel;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.managers.SketchesManager;
import com.blackteam.dsketches.models.Player;
import com.blackteam.dsketches.models.Skill;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.HashSet;
import java.util.Observer;
import java.util.Set;
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
        mWorld.render(graphics);
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
        Log.d("Game", "FRIENDS skill.");

        // У 3-x разные случайных dots.
        Set<Integer> randomDotNumbers = new HashSet<>();
        int iFriend = 0;
        while (iFriend < 3) {
            int randomDotNo = (int) (Math.random() * (mWorld.getNumRows() * mWorld.getNumCols() - 1));

            // Кандидатом на друга становится только тот кто удовлетврояет след. условиям:
            // - еще не был выбран (в рамках текущего использования skill).
            // - не является универсальным.

            boolean isCandidate = randomDotNumbers.add(randomDotNo);

            if (isCandidate) {
                int randomRow = randomDotNo / mWorld.getNumCols();
                int randomCol = randomDotNo % mWorld.getNumCols();
                // Должны быть не универсальными.
                if (mWorld.getDot(randomRow, randomCol).getType() != GameDot.Types.UNIVERSAL) {

                    mWorld.getDot(randomRow, randomCol).setType(GameDot.Types.UNIVERSAL);
                    mWorld.getDot(randomRow, randomCol).startCreatingAnimation();

                    iFriend++;
                    Log.i("Game", String.format("Friend: (%d, %d)", randomRow, randomCol));
                }
            }
        }

        Log.d("Game", String.format("Candidates: {%s}", randomDotNumbers.toString()));
    }

    /**
     * Использовать Skill.Type.RESHUFFLE.
     */
    private void applySkillReshuffle() {
        Log.d("World", "RESHUFFLE skill.");

        for (int iRow = 0; iRow < mWorld.getNumRows(); iRow++) {
            for (int iCol = 0; iCol < mWorld.getNumCols(); iCol++) {
                int randomRow = (int) (Math.random() * mWorld.getNumRows());
                int randomCol = (int) (Math.random() * mWorld.getNumCols());

                mWorld.replaceDots(iRow, iCol, randomRow, randomCol);
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
