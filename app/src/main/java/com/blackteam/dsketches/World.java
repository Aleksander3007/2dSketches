package com.blackteam.dsketches;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.blackteam.dsketches.animation.AnimationController;
import com.blackteam.dsketches.animation.AnimationSet;
import com.blackteam.dsketches.gamedots.GameDot;
import com.blackteam.dsketches.gamedots.GameDotAroundEater;
import com.blackteam.dsketches.gamedots.GameDotColumnEater;
import com.blackteam.dsketches.gamedots.GameDotDouble;
import com.blackteam.dsketches.gamedots.GameDotRowEater;
import com.blackteam.dsketches.gamedots.GameDotTriple;
import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.utils.GameMath;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Модель мира.
 */
public class World extends Observable {

    public static final String TAG = World.class.getSimpleName();

    public static final int DEFAULT_NUM_ROWS = 9;
    public static final int DEFAULT_NUM_COLUMNS = 7;

    public static final String DATA_SKETCH_TYPE = "SKETCH_TYPE";
    public static final String DATA_SKETCH_PROFIT = "PROFIT";
    public static final String DATA_FACTOR = "FACTOR";

    private final SketchesManager mSketchesManager;
    private final ContentManager mContents;

    private Vector2 mPos = new Vector2(0, 0);
    private float mWidth;
    private float mHeight;
    private int mNumRows;
    private int mNumColumns;
    /** Игровые точки. первый идекс - это строки, второй - это столбцы. */
    private GameDot[][] mDots;

    /** Минимальное количество игровых точек, которые можно выделить (меньшее кол-во игнорируется). */
    private static final int MIN_NUM_SELECTED_DOTS = 3;
    private Set<GameDot> selectedDots_ = new CopyOnWriteArraySet<>();
    private Sketch selectedSketch_ = SketchesManager.SKETCH_NULL;

    /** При выделении точки: она увеличивается. */
    private static final float SELECTION_SCALE_ = 1.3f;
    private float dotSize_ = 1.0f;
    private float selectedDotSize_ = 1.0f * SELECTION_SCALE_;

    private boolean isUpdating_ = false;

    /** Время отображения эффекта, мс. */
    private static final float EFFECT_TIME_ = 300f;
    private ArrayList<DisplayableObject> effects_ = new ArrayList<>();

    public World(ContentManager contents, SketchesManager sketchesManager) {
        this.mContents = contents;
        this.mSketchesManager = sketchesManager;

        this.mNumRows = DEFAULT_NUM_ROWS;
        this.mNumColumns = DEFAULT_NUM_COLUMNS;
        this.mDots = new GameDot[mNumRows][mNumColumns];
    }

    public int getNumRows() {
        return mNumRows;
    }

    public int getNumCols() {
        return mNumColumns;
    }

    public synchronized GameDot getDot(int rowNo, int colNo) {
        return mDots[rowNo][colNo];
    }

    public void init(final Vector2 pos, final Size2 rectSize) {
        float dotHeight = rectSize.height / mNumRows;
        float dotWidth = rectSize.width / mNumColumns;

        dotSize_ = (dotWidth < dotHeight) ? dotWidth : dotHeight;
        selectedDotSize_ = dotSize_ * SELECTION_SCALE_; // При выделении точки: она увеличивается.
        GameDot.setAbsTranslateSpeed(dotSize_ / GameDot.TRANSLATE_TIME_);

        this.mHeight = dotSize_ * mNumRows;
        this.mWidth = dotSize_ * mNumColumns;
        this.mPos = new Vector2(pos.x + (rectSize.width / 2) - (this.mWidth / 2), pos.y);

        for (int iRow = 0; iRow < mDots.length; iRow++) {
            for (int iCol = 0; iCol < mDots[iRow].length; iCol++) {
                Vector2 dotPos = convertToPos(iRow, iCol);
                mDots[iRow][iCol].setPosition(dotPos);
                mDots[iRow][iCol].setSize(dotSize_);
            }
        }
    }

    public void render(Graphics graphics) {
        if (!isUpdating_) {
            renderDots(graphics);
            renderEffects(graphics);
        }
    }

    /**
     * Отрисовка игровых точек.
     */
    private void renderDots(Graphics graphics) {
        for (int iRow = 0; iRow < mNumRows; iRow++) {
            for (int iCol = 0; iCol < mNumColumns; iCol++) {
                if (!selectedDots_.contains(mDots[iRow][iCol]))
                    mDots[iRow][iCol].render(graphics);
            }
        }
        // Для отрисовки поверх не выделенных точек.
        for (GameDot dot : selectedDots_) {
            dot.render(graphics);
        }
    }

    /**
     * Отрисовка эффектов.
     */
    private void renderEffects(Graphics graphics) {
        ArrayList<DisplayableObject> finishedEffects = new ArrayList<>();
        for (DisplayableObject effect : effects_) {
            if (!effect.isAnimationFinished())
                effect.render(graphics);
            else
                finishedEffects.add(effect);
        }
        effects_.removeAll(finishedEffects);
        finishedEffects.clear();
    }

    public boolean hit(Vector2 coords) {

        if (hitX(coords.x) && hitY(coords.y)) {
            // Определяем был ли нажат на элемент.
            for (int iRow = 0; iRow < mNumRows; iRow++) {
                for (int iCol = 0; iCol < mNumColumns; iCol++) {
                    if (hitDot(mDots[iRow][iCol], coords))
                        return true;
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    private boolean hitX(float x) {
        return (x >= mPos.x) && (x <= mPos.x + mWidth);
    }

    private boolean hitY(float y) {
        return (y >= mPos.y) && (y <= mPos.y + mHeight);
    }

    private boolean hitDot(GameDot gameDot, Vector2 coords) {
        if (gameDot.hit(coords)) {
            if (!isDotSelected(gameDot)) {
                // Если выбран до этого как минимум еще один.
                if (selectedDots_.size() > 0) {
                    // Если игровые точки одинакового типа и соседи.
                    if (haveGameGotsIdenticalType(selectedDots_, gameDot.getType()) &&
                            doesGameDotHaveNeighbours(selectedDots_, gameDot)) {
                        selectDot(gameDot);
                        selectedDots_.add(gameDot);
                    }
                }
                else {
                    selectDot(gameDot);
                    selectedDots_.add(gameDot);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Выделить указанную игровую точку.
     */
    private void selectDot(GameDot gameDot) {
        gameDot.setSizeCenter(selectedDotSize_);
    }

    /**
     * Являются ли указанная игровая точка кому-нибудь соседом из представленного списка точек.
     * @param gameDots список игровых точек.
     * @param gameDot вторая игровая точка.
     * @return true - если являются соседями.
     */
    private boolean doesGameDotHaveNeighbours(Iterable<GameDot> gameDots, GameDot gameDot) {
        for (GameDot dot : gameDots) {
            if (areGameDotsNeighbours(gameDot, dot)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Являются ли игровые точки соседями.
     * @param gameDot1 первая игровая точка.
     * @param gameDot2 вторая игровая точка.
     * @return true - если являются соседями.
     */
    private boolean areGameDotsNeighbours(GameDot gameDot1, GameDot gameDot2) {
        boolean rowNeighbour = (gameDot1.getColNo() == gameDot2.getColNo()) &&
                (Math.abs((gameDot1.getRowNo() - gameDot2.getRowNo())) == 1);

        boolean columnNeighbour = (gameDot1.getRowNo() == gameDot2.getRowNo()) &&
                (Math.abs((gameDot1.getColNo() - gameDot2.getColNo())) == 1);

        boolean diagonalNeighbour = (Math.abs((gameDot1.getRowNo() - gameDot2.getRowNo())) == 1) &&
                (Math.abs((gameDot1.getColNo() - gameDot2.getColNo())) == 1);

        return rowNeighbour || columnNeighbour || diagonalNeighbour;
    }

    /**
     * Все ли указанные точки имеют указанный тип.
     * @param gameDots точки, которые необходимо проверить.
     * @param dotType  проверяемый тип.
     * @return true - если все точки имеют указанный тип.
     */
    private boolean haveGameGotsIdenticalType(Iterable<GameDot> gameDots, GameDot.Types dotType) {
        for (GameDot dot : gameDots) {
            if (!dot.isIdenticalType(dotType)) return false;
        }
        return true;
    }

    /**
     * Тут идёт анализ выделенных точек.
     * Поиск скетчей, спец. точек и т.п.
     */
    public void update() {
        isUpdating_ = true;

        if (selectedDots_.size() >= MIN_NUM_SELECTED_DOTS) {
            selectedSketch_ = mSketchesManager.findSketch(selectedDots_);
            dotsAffect(selectedDots_);
        }

        isUpdating_ = false;
    }

    /**
     * Оказывать воздействие на игровые точки выделенными точками, если такое свойство у них имеется.
     * @param selectedDots выделенные игровые точки, которые могут оказывать воздействие на другие.
     */
    private void dotsAffect(Set<GameDot> selectedDots) {

        if (selectedDots == null) return;

        for (GameDot gameDot : selectedDots) {

            Set<GameDot> underAffectGameDots = gameDot.affectDots(mDots);
            if (underAffectGameDots == null) continue;

            boolean isNewAdded = selectedDots_.addAll(underAffectGameDots);
            // Если были выделены новые точки, то ищем среди новых.
            if (isNewAdded) {
                dotsAffect(underAffectGameDots);
            }
        }
    }

    /**
     * Находится ли индексы за пределами диапазона.
     * @param rowNo Номер строки.
     * @param colNo Номер столбца.
     * @return true - если за пределами.
     */
    private boolean isOutOfBounds(final int rowNo, final int colNo) {
        return ((rowNo < 0) || (rowNo >= mNumRows) ||
                (colNo < 0) || (colNo >= mNumColumns));
    }

    public int getProfitByDots() {

        if (selectedDots_.size() < MIN_NUM_SELECTED_DOTS) {
            return 0;
        }

        int profit = selectedDots_.size() * GameDot.COST;
        int factor = 1; // TODO: Вынести в переменную FACTOR_DEFAULT = 1;
        for (GameDot gameDot : selectedDots_) {
            factor = gameDot.affectFactor(factor);
        }

        int totalProfit = profit * factor + selectedSketch_.getCost();

        Log.d(TAG, String.format("(profit, factor, sketch) = (%d, %d, %d)",
                profit, factor, selectedSketch_.getCost()));
        Log.d(TAG, "sketch's type = " + selectedSketch_.getName());

        if (selectedSketch_.getName() != null) {
            // TODO: Для этого есть BUNDLE.
            ArrayMap<String, Object> info = new ArrayMap<>();
            info.put(DATA_SKETCH_TYPE, selectedSketch_.getName());
            info.put(DATA_SKETCH_PROFIT, totalProfit);
            info.put(DATA_FACTOR, factor);

            // Оповещаем, что есть изменения.
            setChanged();
            notifyObservers(info);
        }

        return totalProfit;
    }

    /**
     * Снять выделение.
     */
    public void removeSelection() {
        for (GameDot dot : selectedDots_) {
            dot.setSizeCenter(dotSize_);
        }
        selectedDots_.clear();
        selectedSketch_ = SketchesManager.SKETCH_NULL;
    }

    /**
     * Удалить выделенные Dots.
     */
    public void deleteSelectedDots() {
        deleteDots(selectedDots_);
    }

    /**
     * Удалить указанные Dots.
     * @param dots Которые необходимо удалить.
     */
    public void deleteDots(Iterable<GameDot> dots) {
        isUpdating_ = true;

        for (GameDot gameDot : dots) {

            addEffect(gameDot.getSpecType(), gameDot.getPosition());

            int translateCol = gameDot.getColNo();
            for (int iRow = gameDot.getRowNo() + 1; iRow < mNumRows; iRow++) {
                mDots[iRow][translateCol].moveTo(convertToPos(iRow - 1, translateCol));
                mDots[iRow][translateCol].setRowNo(iRow - 1); // TODO: Row хранится в двух местах - ОЧЕНЬ ПЛОХО!
                mDots[iRow - 1][translateCol] = mDots[iRow][translateCol];
            }
            createDot(mNumRows - 1, translateCol);
        }

        isUpdating_ = false;
    }
    /**
     * Отобразить эффект от специальной игровой точки.
     * @param dotSpecType Тип специальной игровой точки.
     * @param dotPos Позиция игровой точки, которая порадила эффект.
     */
    private void addEffect(GameDot.SpecTypes dotSpecType, Vector2 dotPos) {
        Size2 newSize;
        switch (dotSpecType) {
            case ROW_EATER:
                newSize = new Size2(dotSize_ * 2 * mNumColumns, dotSize_);
                break;
            case COLUMN_EATER:
                newSize = new Size2(dotSize_, dotSize_ * 2 * mNumRows);
                break;
            case AROUND_EATER:
                newSize = new Size2(3 * dotSize_, 3 * dotSize_);
                break;
            default:
                // по умолчанию никаких визуальных эффектов нет.
                return;
        }

        TextureRegion textureRegion = new TextureRegion(
                mContents.get(R.drawable.dots_theme1),
                GameDot.getSpecTexturePosition(dotSpecType),
                new Size2(GameDot.TEX_WIDTH, GameDot.TEX_HEIGHT)
        );
        DisplayableObject effect = new DisplayableObject(textureRegion);
        effect.setSize(dotSize_, dotSize_);
        effect.setPosition(dotPos);

        AnimationSet animSet = new AnimationSet(AnimationSet.ValueType.SCALE_CENTER,
                AnimationSet.PlayMode.NORMAL,
                new Vector2(dotSize_, dotSize_),
                new Vector2(newSize.width, newSize.height),
                EFFECT_TIME_);
        effect.setAnimation(new AnimationController(animSet));

        effects_.add(effect);
    }

    /**
     * Создание уровня с параметрами по умолчанию.
     */
    public void createLevel() {
        createLevel(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLUMNS);
    }

    public void createLevel(final int nRow, final int nColumn) {
        this.mNumRows = nRow;
        this.mNumColumns = nColumn;

        for (int iRow = 0; iRow < mNumRows; iRow++) {
            for (int iCol = 0; iCol < mNumColumns; iCol++) {
                createDot(iRow, iCol);
            }
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Level is created.");
        }
    }

    public void createLevel(GameDot[][] gameDots) {
        mNumRows = gameDots.length;
        mNumColumns = gameDots[0].length;

        mDots = gameDots;
        for (int iRow = 0; iRow < mNumRows; iRow++) {
            for (int iCol = 0; iCol < mNumColumns; iCol++) {
                if (gameDots[iRow][iCol] != null) {
                    createDot(gameDots[iRow][iCol].getType(),
                            gameDots[iRow][iCol].getSpecType(),
                            iRow, iCol);
                }
                // Если у нас по какой-то причине не инилизирована точка,
                // то создаем случайную.
                else {
                    createDot(iRow, iCol);
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Log.i("World", "Level is created.");
        }
    }

    private GameDot.Types generateDotType() {
        // TODO: Подумать где дожна находится карта вероятностей выпадения. (GameRuler?)
        ArrayMap<GameDot.Types, Float> dotTypeProbabilities = new ArrayMap<>();
        dotTypeProbabilities.put(GameDot.Types.TYPE1, 24f);
        dotTypeProbabilities.put(GameDot.Types.TYPE2, 24f);
        dotTypeProbabilities.put(GameDot.Types.TYPE3, 24f);
        dotTypeProbabilities.put(GameDot.Types.TYPE4, 24f);
        dotTypeProbabilities.put(GameDot.Types.UNIVERSAL, 104f); // original: 4f

        return GameMath.generateValue(dotTypeProbabilities);
    }

    private GameDot.SpecTypes generateDotSpecType() {
        // TODO: Подумать где дожна находится карта вероятностей выпадения. (GameRuler?)
        ArrayMap<GameDot.SpecTypes, Float> dotTypeProbabilities = new ArrayMap<>();
        dotTypeProbabilities.put(GameDot.SpecTypes.NONE, 93f);
        dotTypeProbabilities.put(GameDot.SpecTypes.DOUBLE, 2f);
        dotTypeProbabilities.put(GameDot.SpecTypes.TRIPLE, 0.5f);
        dotTypeProbabilities.put(GameDot.SpecTypes.ROW_EATER, 1.5f);
        dotTypeProbabilities.put(GameDot.SpecTypes.COLUMN_EATER, 1.5f);
        dotTypeProbabilities.put(GameDot.SpecTypes.AROUND_EATER, 1.5f); // original: 1.5f

        return GameMath.generateValue(dotTypeProbabilities);
    }

    private boolean isDotSelected(GameDot gameDot) {
        for (GameDot selectedGameDot : selectedDots_) {
            if (gameDot == selectedGameDot) {
                return true;
            }
        }
        return false;
    }

    /**
     * Создать GameDot.
     * @param rowNo Номер строки.
     * @param colNo Номер столбца.
     */
    private void createDot(final int rowNo, final int colNo) {
        GameDot.Types dotType = generateDotType();
        GameDot.SpecTypes dotSpecType = generateDotSpecType();
        createDot(dotType, dotSpecType, rowNo, colNo);
        Log.i(TAG, String.format("There is created specType = %s", dotSpecType.toString()));
    }

    /**
     * Создать GameDot.
     * @param dotType Тип.
     * @param dotSpecType Специальный тип.
     * @param rowNo Номер строки.
     * @param colNo Номер столбца.
     */
    public void createDot(final GameDot.Types dotType, final GameDot.SpecTypes dotSpecType,
                          final int rowNo, final int colNo
    ) {
        Vector2 dotPos = new Vector2(
                this.mPos.x + colNo * dotSize_,
                this.mPos.y + rowNo * dotSize_);

        switch (dotSpecType) {
            case DOUBLE:
                mDots[rowNo][colNo] = new GameDotDouble(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        mContents
                );
                break;
            case TRIPLE:
                mDots[rowNo][colNo] = new GameDotTriple(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        mContents
                );
                break;
            case ROW_EATER:
                mDots[rowNo][colNo] = new GameDotRowEater(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        mContents
                );
                break;
            case COLUMN_EATER:
                mDots[rowNo][colNo] = new GameDotColumnEater(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        mContents
                );
                break;
            case AROUND_EATER:
                mDots[rowNo][colNo] = new GameDotAroundEater(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        mContents
                );
                break;
            default:
                mDots[rowNo][colNo] = new GameDot(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        mContents
                );
        }

        mDots[rowNo][colNo].setSize(dotSize_);
    }

    /**
     * Установка выделенных GameDot.
     * <br><strong>NOTE</strong>: Только для тестирования.
     */
    protected void setSelectedDots(Set<GameDot> gameDots) {
        selectedDots_ = gameDots;
    }

    private Vector2 convertToPos(final int rowNo, final int colNo) {
        return new Vector2(
                this.mPos.x + colNo * dotSize_,
                this.mPos.y + rowNo * dotSize_);
    }
}
