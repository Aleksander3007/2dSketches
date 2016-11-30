package com.blackteam.dsketches;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.blackteam.dsketches.animation.AnimationController;
import com.blackteam.dsketches.animation.AnimationSet;
import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.utils.GameMath;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Модель мира.
 */
public class World extends Observable {
    public static final int DEFAULT_NUM_ROWS = 9;
    public static final int DEFAULT_NUM_COLUMNS = 7;

    private SketchesManager sketchesManager_;
    private ContentManager contents_;

    private Vector2 pos_ = new Vector2(0, 0);
    private float width_;
    private float height_;
    private int nRows_;
    private int nColumns_;
    private GameDot[][] dots_;

    private CopyOnWriteArrayList<GameDot> selectedDots_ = new CopyOnWriteArrayList<>();
    private Sketch selectedSketch_ = SketchesManager.SKETCH_NULL_;

    /** При выделении точки: она увеличивается. */
    private static final float SELECTION_SCALE_ = 1.3f;
    private float dotSize_ = 1.0f;
    private float selectedDotSize_ = 1.0f * SELECTION_SCALE_;

    private boolean isUpdating_ = false;

    /** Время отображения эффекта, мс. */
    private static final float EFFECT_TIME_ = 200f;
    private ArrayList<DisplayableObject> effects_ = new ArrayList<>();

    public World(ContentManager contents, SketchesManager sketchesManager) {
        this.contents_ = contents;
        this.sketchesManager_ = sketchesManager;

        this.nRows_ = DEFAULT_NUM_ROWS;
        this.nColumns_ = DEFAULT_NUM_COLUMNS;
        this.dots_ = new GameDot[nRows_][nColumns_];
    }

    public int getNumRows() {
        return nRows_;
    }

    public int getNumCols() {
        return nColumns_;
    }

    public synchronized GameDot getDot(int rowNo, int colNo) {
        return dots_[rowNo][colNo];
    }

    public void init(final Vector2 pos, final Size2 rectSize) {
        float dotHeight = rectSize.height / nRows_;
        float dotWidth = rectSize.width / nColumns_;

        dotSize_ = (dotWidth < dotHeight) ? dotWidth : dotHeight;
        selectedDotSize_ = dotSize_ * SELECTION_SCALE_; // При выделении точки: она увеличивается.
        GameDot.setAbsTranslateSpeed(dotSize_ / GameDot.TRANSLATE_TIME_);

        this.height_ = dotSize_ * nRows_;
        this.width_ = dotSize_ * nColumns_;
        this.pos_ = new Vector2(pos.x + (rectSize.width / 2) - (this.width_ / 2), pos.y);

        for (int iRow = 0; iRow < dots_.length; iRow++) {
            for (int iCol = 0; iCol < dots_[iRow].length; iCol++) {
                Vector2 dotPos = convertToPos(iRow, iCol);
                dots_[iRow][iCol].setPosition(dotPos);
                dots_[iRow][iCol].setSize(dotSize_);
            }
        }
    }

    public void draw(Graphics graphics) {
        if (!isUpdating_) {
            for (int iRow = 0; iRow < nRows_; iRow++) {
                for (int iCol = 0; iCol < nColumns_; iCol++) {
                    if (!selectedDots_.contains(dots_[iRow][iCol]))
                        dots_[iRow][iCol].draw(graphics);
                }
            }
            for (GameDot dot : selectedDots_) {
                dot.draw(graphics);
            }

            ArrayList<DisplayableObject> finishedEffects = new ArrayList<>();
            for (DisplayableObject effect : effects_) {
                if (!effect.isAnimationFinished())
                    effect.draw(graphics);
                else {
                    finishedEffects.add(effect);
                }
            }
            effects_.removeAll(finishedEffects);
            finishedEffects.clear();
        }
    }

    public boolean hit(Vector2 coords) {
        boolean hitX = (coords.x >= pos_.x) && (coords.x <= pos_.x + width_);
        boolean hitY = (coords.y >= pos_.y) && (coords.y <= pos_.y + height_);

        if (hitX && hitY) {
            // Определяем был ли нажат на элемент.
            for (int iRow = 0; iRow < nRows_; iRow++) {
                for (int iCol = 0; iCol < nColumns_; iCol++) {
                    if (hitDot(dots_[iRow][iCol], coords))
                        return true;
                }
            }
        }

        return (hitX && hitY);
    }

    private boolean hitDot(GameDot gameDot, Vector2 coords) {
        if (gameDot.hit(coords)) {
            if (!isDotSelected(gameDot)) {
                // Если выбран до этого как минимум еще один.
                if (selectedDots_.size() > 0) {
                    GameDot prevGameDot = selectedDots_.get(selectedDots_.size() - 1);

                    // Если соседний, то выделяем (плюс защита от нажатий несколькими пальцами в разных местах).
                    boolean rowNeighbour = (gameDot.getColNo() == prevGameDot.getColNo()) &&
                            (Math.abs((gameDot.getRowNo() - prevGameDot.getRowNo())) == 1);
                    boolean columnNeighbour = (gameDot.getRowNo() == prevGameDot.getRowNo()) &&
                            (Math.abs((gameDot.getColNo() - prevGameDot.getColNo())) == 1);
                    boolean diagonalNeighbour = (Math.abs((gameDot.getRowNo() - prevGameDot.getRowNo())) == 1) &&
                            (Math.abs((gameDot.getColNo() - prevGameDot.getColNo())) == 1);

                    boolean isIdenticalType = true;
                    GameDot.Types dotType = gameDot.getType();
                    for (GameDot dot : selectedDots_) {
                        if (!dot.isIdenticalType(dotType)) {
                            isIdenticalType = false;
                            break;
                        }
                    }

                    if (isIdenticalType && (rowNeighbour || columnNeighbour || diagonalNeighbour)) {

                        gameDot.setSizeCenter(selectedDotSize_);
                        selectedDots_.add(gameDot);
                    }
                }
                else {
                    gameDot.setSizeCenter(selectedDotSize_);
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
     * Тут идёт анализ выделенных точек.
     * Поиск скетчей, спец. точек и т.п.
     */
    public void update() {
        isUpdating_ = true;

        if (selectedDots_.size() > 2) {
            selectedSketch_ = sketchesManager_.findSketch(selectedDots_);
            // Ищем спец. Dots.
            searchSpecDots(selectedDots_);
        }

        isUpdating_ = false;
    }

    private List<GameDot> searchSpecDots(List<GameDot> selectedDots) {
        ArrayList<GameDot> addSpecDots_ = new ArrayList<>();
        for (GameDot gameDot : selectedDots) {
            switch (gameDot.getSpecType()) {
                case ROW_EATER: {
                    // Добавляем все элементы строки как выделенные.
                    for (int iCol = 0; iCol < nColumns_; iCol++) {
                        // ... без повтора в массиве.
                        if (!selectedDots_.contains(dots_[gameDot.getRowNo()][iCol])) {
                            // ... и делаем их уникальным, чтобы считался Profit и для них.
                            dots_[gameDot.getRowNo()][iCol].setType(GameDot.Types.UNIVERSAL);
                            addSpecDots_.add(dots_[gameDot.getRowNo()][iCol]);
                        }
                    }
                    selectedDots_.addAll(addSpecDots_);
                    // Среди только что добавленных точек ищем спец. точки.
                    addSpecDots_.addAll(searchSpecDots(addSpecDots_));
                    break;
                }
                case COLUMN_EATER: {
                    // Тоже самое, что ROW_EATER, только столбец (см. выше).
                    for (int iRow = 0; iRow < nRows_; iRow++) {
                        if (!selectedDots_.contains(dots_[iRow][gameDot.getColNo()])) {
                            dots_[iRow][gameDot.getColNo()].setType(GameDot.Types.UNIVERSAL);
                            addSpecDots_.add(dots_[iRow][gameDot.getColNo()]);
                        }
                    }
                    selectedDots_.addAll(addSpecDots_);
                    addSpecDots_.addAll(searchSpecDots(addSpecDots_));
                    break;
                }
                case AROUND_EATER: {
                    // Добавляем все соседние элементы как выделенные.
                    for (int iRow = -1; iRow <= 1; iRow++) {
                        for (int iCol = -1; iCol <= 1; iCol++) {
                            int rowNo = gameDot.getRowNo() + iRow;
                            int colNo = gameDot.getColNo() + iCol;
                            if (!isOutOfBounds(rowNo, colNo)) {
                                if (!selectedDots_.contains(dots_[rowNo][colNo])) {
                                    dots_[rowNo][colNo].setType(GameDot.Types.UNIVERSAL);
                                    addSpecDots_.add(dots_[rowNo][colNo]);
                                }
                            }
                        }
                    }
                    selectedDots_.addAll(addSpecDots_);
                    addSpecDots_.addAll(searchSpecDots(addSpecDots_));
                    break;
                }
                default:
                    // В остальных случаях ничего не делаем.
                    break;
            }
        }

        return addSpecDots_;
    }

    /**
     * Находится ли индексы за пределами диапазона.
     * @param rowNo Номер строки.
     * @param colNo Номер столбца.
     * @return true - если за пределами.
     */
    public boolean isOutOfBounds(final int rowNo, final int colNo) {
        return ((rowNo < 0) || (rowNo >= nRows_) ||
                (colNo < 0) || (colNo >= nColumns_));
    }

    public int getProfitByDots() {

        if (selectedDots_.size() <= 2) {
            return 0;
        }

        int profit = selectedDots_.size() * GameDot.COST;
        int factor = 1;
        for (GameDot gameDot : selectedDots_) {
            switch (gameDot.getSpecType()) {
                case DOUBLE: {
                    factor *= 2;
                    break;
                }
                case TRIPLE: {
                    factor *= 3;
                    break;
                }
                default:
                    // В остальных случаях ничего не делаем.
                    break;
            }
        }

        int totalProfit = profit * factor + selectedSketch_.getCost();
        Log.i("World", "(profit, factor, sketch) = " +
                "(" +
                String.valueOf(profit) + "," +
                String.valueOf(factor) + "," +
                String.valueOf(selectedSketch_.getCost()) + "," +
                ")"
        );
        Log.i("World", "sketch's type = " + selectedSketch_.getName().toString());
        if (selectedSketch_.getName() != null) {
            ArrayMap<String, Object> info = new ArrayMap<>();
            info.put("SketchType", selectedSketch_.getName());
            info.put("Profit", totalProfit);
            info.put("Factor", factor);

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
        selectedSketch_ = SketchesManager.SKETCH_NULL_;
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
    public void deleteDots(CopyOnWriteArrayList<GameDot> dots) {
        isUpdating_ = true;

        for (GameDot gameDot : dots) {

            addEffect(gameDot.getSpecType(), gameDot.getPosition());

            int translateCol = gameDot.getColNo();
            for (int iRow = gameDot.getRowNo() + 1; iRow < nRows_; iRow++) {
                dots_[iRow][translateCol].moveTo(convertToPos(iRow - 1, translateCol));
                dots_[iRow][translateCol].setRowNo(iRow - 1); // TODO: Row хранится в двух местах - ОЧЕНЬ ПЛОХО!
                dots_[iRow - 1][translateCol] = dots_[iRow][translateCol];
            }
            createDot(nRows_ - 1, translateCol);
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
        float speed;
        switch (dotSpecType) {
            case NONE:
                return;
            case DOUBLE:
                return;
            case TRIPLE:
                return;
            case ROW_EATER:
                newSize = new Size2(dotSize_ * 2 * nColumns_, dotSize_);
                speed = (dotSize_ * 2 * nColumns_ - dotSize_) / EFFECT_TIME_;
                break;
            case COLUMN_EATER:
                newSize = new Size2(dotSize_, dotSize_ * 2 * nRows_);
                speed = (dotSize_ * 2 * nRows_ - dotSize_) / EFFECT_TIME_;
                break;
            case AROUND_EATER:
                newSize = new Size2(3 * dotSize_, 3 * dotSize_);
                speed = (2 * dotSize_) / EFFECT_TIME_;
                break;
            default:
                return;
        }

        TextureRegion textureRegion = new TextureRegion(
                contents_.get(R.drawable.dots_theme1),
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
                speed);
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
        this.nRows_ = nRow;
        this.nColumns_ = nColumn;

        for (int iRow = 0; iRow < nRows_; iRow++) {
            for (int iCol = 0; iCol < nColumns_; iCol++) {
                createDot(iRow, iCol);
            }
        }

        if (BuildConfig.DEBUG) {
            Log.i("World", "Level is created.");
        }
    }

    public void createLevel(GameDot[][] gameDots) {
        nRows_ = gameDots.length;
        nColumns_ = gameDots[0].length;

        dots_ = gameDots;
        for (int iRow = 0; iRow < nRows_; iRow++) {
            for (int iCol = 0; iCol < nColumns_; iCol++) {
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
        dotTypeProbabilities.put(GameDot.Types.UNIVERSAL, 104f); // 4f

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
        dotTypeProbabilities.put(GameDot.SpecTypes.AROUND_EATER, 1.5f);

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
                this.pos_.x + colNo * dotSize_,
                this.pos_.y + rowNo * dotSize_);
        dots_[rowNo][colNo] = new GameDot(dotType, dotSpecType,
                dotPos,
                rowNo, colNo,
                contents_
        );

        dots_[rowNo][colNo].setSize(dotSize_);
    }

    /**
     * Установка выделенных GameDot.
     * <br><strong>NOTE</strong>: Только для тестирования.
     */
    protected void setSelectedDots(CopyOnWriteArrayList<GameDot> gameDots) {
        selectedDots_ = gameDots;
    }

    private Vector2 convertToPos(final int rowNo, final int colNo) {
        return new Vector2(
                this.pos_.x + colNo * dotSize_,
                this.pos_.y + rowNo * dotSize_);
    }
}
