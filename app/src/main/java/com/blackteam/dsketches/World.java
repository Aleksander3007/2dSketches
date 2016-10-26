package com.blackteam.dsketches;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.utils.GameMath;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Модель мира.
 */
public class World extends Observable {
    public static final int DEFAULT_NUM_ROWS = 9;
    public static final int DEFAULT_NUM_COLUMNS = 7;

    private Texture touchLineTexture_;
    private final ArrayMap<GameDot.Types, ArrayMap<GameDot.SpecTypes, Texture>> dotTextures_ = new ArrayMap<>();

    private Vector2 pos_ = new Vector2(0, 0);
    private float width_;
    private float height_;
    private int nRows_;
    private int nColumns_;
    private GameDot[][] dots_;
    private ArrayList<GameDot> selectedDots_ = new ArrayList<>();
    private CopyOnWriteArrayList<TouchLine> touchLines_ = new CopyOnWriteArrayList<>();
    private Sketch selectedSketch_ = SketchesManager.SKETCH_NULL_;

    private float dotSize_ = 1.0f;
    private Size2 touchLineSize_;

    private boolean isUpdating_ = false;

    private SketchesManager sketchesManager_;

    public World(ContentManager contents) {
        nRows_ = DEFAULT_NUM_ROWS;
        nColumns_ = DEFAULT_NUM_COLUMNS;
        dots_ = new GameDot[nRows_][nColumns_];
        sketchesManager_ = new SketchesManager();

        loadContent(contents);
    }

    public void init(final Vector2 pos, final Size2 rectSize) {
        float dotHeight = rectSize.height / nRows_;
        float dotWidth = rectSize.width / nColumns_;

        dotSize_ = (dotWidth < dotHeight) ? dotWidth : dotHeight;

        this.height_ = dotSize_ * nRows_;
        this.width_ = dotSize_ * nColumns_;
        this.pos_ = new Vector2(pos.x + (rectSize.width / 2) - (this.width_ / 2), pos.y);

        touchLineSize_ = new Size2(
                dotSize_, /* (GameDot.WIDTH / 2) + (GameDot.WIDTH / 2) */
                dotSize_ / 4.0f
        );

        Log.i("World", "dots.length = " + String.valueOf(dots_.length));
        for (int iRow = 0; iRow < dots_.length; iRow++) {
            for (int iCol = 0; iCol < dots_[iRow].length; iCol++) {
                Vector2 dotPos = new Vector2(
                        this.pos_.x + iCol * dotSize_,
                        this.pos_.y + iRow * dotSize_);
                dots_[iRow][iCol].setPosition(dotPos);
                dots_[iRow][iCol].setSize(dotSize_);
            }
        }
    }

    public void draw(float[] mvpMatrix, final ShaderProgram shader) {
        if (!isUpdating_) {
            for (int iRow = 0; iRow < nRows_; iRow++) {
                for (int iCol = 0; iCol < nColumns_; iCol++) {
                    dots_[iRow][iCol].draw(mvpMatrix, shader);
                }
            }
            for (TouchLine touchLine : touchLines_) {
                touchLine.draw(mvpMatrix, shader);
            }
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

    public int getNumRows() {
        return nRows_;
    }

    public int getNumCols() {
        return nColumns_;
    }

    public GameDot getDot(int rowNo, int colNo) {
        return dots_[rowNo][colNo];
    }

    public int getProfitByDots() {
        // TODO: Возможно это должно быть в классе GameRules.
        // А лучше GameRule, и GameRuleManager.
        if (selectedDots_.size() <= 2) {
            return 0;
        }

        int profit = 0;
        int factor = 1;
        GameDot.Types dotType = selectedDots_.get(0).getType();
        for (GameDot gameDot : selectedDots_) {
            if ((gameDot.getType() == dotType) ||
                    (gameDot.getType() == GameDot.Types.UNIVERSAL) || (dotType == GameDot.Types.UNIVERSAL)) {
                profit += 10; // TODO: Magic number!
            }
            // Все элементы должны быть одинакового GameDot's Type.
            else {
                return 0;
            }

            switch (gameDot.getSpecType()) {
                case DOUBLE: {
                    factor *= 2;
                    break;
                }
                case TRIPLE: {
                    factor *= 3;
                    break;
                }
                case ROWS_EATER: {
                    // Элементы должны тоже считаться.
                    break;
                }
                default:
                    // В остальных случаях ничего не делаем.
                    break;
            }

            dotType = gameDot.getType();
        }

        Log.i("World", "(profit, factor, sketch) = " +
                "(" +
                String.valueOf(profit) + "," +
                String.valueOf(factor) + "," +
                String.valueOf(selectedSketch_.getCost()) +
                ")"
        );

        return (profit * factor + selectedSketch_.getCost());
    }

    public void update() {
        isUpdating_ = true;

        if (selectedDots_.size() >= 2) {

            selectedSketch_ = sketchesManager_.findSketch(selectedDots_);

            Log.i("World", "sketch's type = " + selectedSketch_.getType().toString());
            if (selectedSketch_.getType() != Sketch.Types.NONE) {
                setChanged();
                notifyObservers(selectedSketch_.getType());
            }

            // Добавленные с помощью спец. Dots.
            ArrayList<GameDot> addSpecDots_ = new ArrayList<>();
            // Ищем спец. Dots.
            for (GameDot gameDot : selectedDots_) {
                switch (gameDot.getSpecType()) {
                    case ROWS_EATER: {
                        // Добавляем все элементы строки как выделенные.
                        for (int iCol = 0; iCol < nColumns_; iCol++) {
                            // ... без повтора в массиве.
                            if (!selectedDots_.contains(dots_[gameDot.getRowNo()][iCol])) {
                                // ... и делаем их уникальным, чтобы считался Profit и для них.
                                dots_[gameDot.getRowNo()][iCol].setType(GameDot.Types.UNIVERSAL);
                                addSpecDots_.add(dots_[gameDot.getRowNo()][iCol]);
                            }
                        }
                    }
                    default:
                        // В остальных случаях ничего не делаем.
                        break;
                }
            }

            selectedDots_.addAll(addSpecDots_);
        }

        isUpdating_ = false;
    }

    /**
     * Снять выделение.
     */
    public void removeSelection() {
        touchLines_.clear();
        selectedDots_.clear();
        selectedSketch_ = SketchesManager.SKETCH_NULL_;
    }

    /**
     * Удалить выделенные Dots.
     */
    public void deleteSelectedDots() {
        // Определяем верхних соседей.
        for (GameDot gameDot : selectedDots_) {
            for (int iRow = gameDot.getRowNo() + 1; iRow < nRows_; iRow++) {
                // Опускаем все элементы колонки на клетку ниже.
                createDot(dots_[iRow][gameDot.getColNo()].getType(),
                        dots_[iRow][gameDot.getColNo()].getSpecType(),
                        iRow - 1, gameDot.getColNo());
            }

            // На пустую верхную часть генерируем новые.
            createDot(nRows_ - 1, gameDot.getColNo());
        }
    }

    public void dispose() {
        touchLines_.clear();
    }

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

    private GameDot.Types generateDotType() {
        // TODO: Подумать где дожна находится карта вероятностей выпадения. (GameRuler?)
        ArrayMap<GameDot.Types, Float> dotTypeProbabilities = new ArrayMap<>();
        dotTypeProbabilities.put(GameDot.Types.TYPE1, 32f);
        dotTypeProbabilities.put(GameDot.Types.TYPE2, 32f);
        dotTypeProbabilities.put(GameDot.Types.TYPE3, 32f);
        dotTypeProbabilities.put(GameDot.Types.UNIVERSAL, 4f);

        return GameMath.generateValue(dotTypeProbabilities);
    }

    private GameDot.SpecTypes generateDotSpecType() {
        // TODO: Подумать где дожна находится карта вероятностей выпадения. (GameRuler?)
        ArrayMap<GameDot.SpecTypes, Float> dotTypeProbabilities = new ArrayMap<>();
        dotTypeProbabilities.put(GameDot.SpecTypes.NONE, 80f);
        dotTypeProbabilities.put(GameDot.SpecTypes.DOUBLE, 10f);
        dotTypeProbabilities.put(GameDot.SpecTypes.TRIPLE, 0f);
        dotTypeProbabilities.put(GameDot.SpecTypes.AROUND_EATER, 0f);
        dotTypeProbabilities.put(GameDot.SpecTypes.ROWS_EATER, 10f);
        dotTypeProbabilities.put(GameDot.SpecTypes.COLUMNS_EATER, 0f);

        return GameMath.generateValue(dotTypeProbabilities);
    }

    private boolean hitDot(GameDot gameDot, Vector2 coords) {
        if (gameDot.hit(coords)) {
            if (!isDotSelected(gameDot)) {
                // Если выбран до этого как минимум еще один.
                if (selectedDots_.size() > 0) {
                    GameDot prevGameDot = selectedDots_.get(selectedDots_.size() - 1);

                    // Если соседний, то выделяем (защита от нажатий несколькими пальцами в разных местах).
                    if ((Math.abs((gameDot.getColNo() - prevGameDot.getColNo())) == 1) ||
                            (Math.abs((gameDot.getRowNo() - prevGameDot.getRowNo())) == 1)) {
                        selectedDots_.add(gameDot);

                        TouchLine touchLine = new TouchLine(
                                prevGameDot, gameDot,
                                touchLineSize_,
                                touchLineTexture_
                        );
                        touchLines_.add(touchLine);
                    }
                }
                else {
                    selectedDots_.add(gameDot);
                }
            }

            return true;
        }
        else {
            return false;
        }
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
        Texture dotTexture = dotTextures_.get(dotType).get(dotSpecType);
        Vector2 dotPos = new Vector2(
                this.pos_.x + colNo * dotSize_,
                this.pos_.y + rowNo * dotSize_);
        dots_[rowNo][colNo] = new GameDot(dotType, dotSpecType,
                dotPos,
                rowNo, colNo,
                dotTexture
        );

        dots_[rowNo][colNo].setSize(dotSize_);
    }

    /**
     * Установка выделенных GameDot (Для тестирования).
     */
    protected void setSelectedDots(ArrayList<GameDot> gameDots) {
        selectedDots_ = gameDots;
    }

    public void loadContent(ContentManager contents) {
        for (GameDot.Types dotType : GameDot.Types.values()) {
            for (GameDot.SpecTypes dotSpecType : GameDot.SpecTypes.values()) {

                int dotResourceId = GameDot.getResourceId(dotType, dotSpecType);
                Texture dotTexture = contents.get(dotResourceId);

                if (dotTextures_.get(dotType) != null) {
                    dotTextures_.get(dotType).put(dotSpecType, dotTexture);
                }
                else {
                    ArrayMap<GameDot.SpecTypes, Texture> dotSpecTypeTextures = new ArrayMap<>();
                    dotSpecTypeTextures.put(dotSpecType, dotTexture);
                    dotTextures_.put(dotType, dotSpecTypeTextures);
                }
            }
        }

        touchLineTexture_ = contents.get(TouchLine.getResourceId());

        if (BuildConfig.DEBUG)
            Log.i("World.Content", "Content of the world are loaded.");
    }
}
