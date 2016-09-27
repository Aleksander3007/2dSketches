package com.blackteam.dsketches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.blackteam.tripledouble.R;

import java.util.ArrayList;

/**
 * Created by Aleksander on 26.09.2016.
 */
public class World {
    private Bitmap touchLineTexture_;
    private ArrayMap<OrbType, ArrayMap<OrbSpecType, Bitmap>> orbTextures_ =
            new ArrayMap<OrbType, ArrayMap<OrbSpecType, Bitmap>>();

    private Vector2 pos_;
    private float width_;
    private float height_;
    private int nRows_;
    private int nColumns_;
    private Orb[][] orbs_;
    private ArrayList<Orb> selectedOrbs_ = new ArrayList<Orb>();
    private ArrayList<TouchLine> touchLines_ = new ArrayList<TouchLine>();

    public World(final Vector2 pos, final int rectWidth, final int rectHeight, ArrayMap<String, Bitmap> bitmaps) {
        this.pos_ = pos;

        this.nColumns_ = (int) Math.floor(rectWidth / Orb.WIDTH);
        this.nRows_ = (int) Math.floor(rectHeight  / Orb.HEIGHT);
        this.height_ = Orb.WIDTH * this.nRows_;
        this.width_ = Orb.HEIGHT * this.nColumns_;
    }

    public void init() {
        createLevel();
    }

    public void onDraw(Canvas canvas) {
        for (int iRow = 0; iRow < nRows_; iRow++) {
            for (int iCol = 0; iCol < nColumns_; iCol++) {
                orbs_[iRow][iCol].onDraw(canvas);
            }
        }
        for (TouchLine touchLine : touchLines_) {
            touchLine.onDraw(canvas);
        }
    }

    public boolean hit(Vector2 coords) {
        boolean hitX = (coords.x >= pos_.x) && (coords.x <= pos_.x + width_);
        boolean hitY = (coords.y >= pos_.y) && (coords.y <= pos_.y + height_);

        if (hitX && hitY) {
            // Определяем был ли нажат на элемент.
            for (int iRow = 0; iRow < nRows_; iRow++) {
                for (int iCol = 0; iCol < nColumns_; iCol++) {
                    if (hitOrb(orbs_[iRow][iCol], coords))
                        return true;
                }
            }
        }

        return (hitX && hitY);
    }

    public int getProfitByOrbs() {
        // TODO: Возможно это должно быть в классе GameRules.
        // А лучше GameRule, и GameRuleManager.
        if (selectedOrbs_.size() < 2) {
            return 0;
        }

        int profit = 0;
        int factor = 1;
        OrbType orbType = selectedOrbs_.get(0).getType();
        for (Orb orb : selectedOrbs_) {
            if ((orb.getType() == orbType) || (orb.getType() == OrbType.UNIVERSAL) || (orbType == OrbType.UNIVERSAL)) {
                profit += 10;
            }
            // Все элементы должны быть одинакового OrbType.
            else {
                return 0;
            }

            switch (orb.getSpecType()) {
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

            orbType = orb.getType();
        }

        return (profit * factor);
    }

    public void update() {
        if (selectedOrbs_.size() < 2) {
            return;
        }

        // Ищем спец. Orbs.
        for (Orb orb : selectedOrbs_) {
            switch (orb.getSpecType()) {
                case ROWS_EATER: {
                    // Добавляем все элементы строки как выделенные.
                    for (int iCol = 0; iCol < nColumns_; iCol++) {
                        // ... без повтора в массиве.
                        if (!selectedOrbs_.contains(orbs_[orb.getRowNo()][iCol])) {
                            // ... и делаем их уникальным, чтобы считался Profit и для них.
                            orbs_[orb.getRowNo()][iCol].setType(OrbType.UNIVERSAL);
                            selectedOrbs_.add(orbs_[orb.getRowNo()][iCol]);
                        }
                    }
                }
                default:
                    // В остальных случаях ничего не делаем.
                    break;
            }
        }
    }

    public void removeSelection() {
        touchLines_.clear();
        selectedOrbs_.clear();
    }

    /**
     * Удалить выделенные Orbs.
     */
    public void deleteSelectedOrbs() {
        // Определяем верхних соседей.
        for (Orb orb : selectedOrbs_) {
            for (int iRow = orb.getRowNo() + 1; iRow < nRows_; iRow++) {
                // Опускаем все элементы колонки на клетку ниже.
                createOrb(orbs_[iRow][orb.getColNo()].getType(),
                        orbs_[iRow][orb.getColNo()].getSpecType(),
                        iRow - 1, orb.getColNo());
            }

            // На пустую верхную часть генерируем новые.
            createOrb(nRows_ - 1, orb.getColNo());
        }
    }

    public void dispose () {
        clearOrbs();

        for (TouchLine touchLine : touchLines_) {
            touchLine.dispose();
        }
        touchLines_.clear();
    }

    public void createLevel() {
        clearOrbs();

        orbs_ = new Orb[nRows_][nColumns_];
        for (int iRow = 0; iRow < nRows_; iRow++) {
            for (int iCol = 0; iCol < nColumns_; iCol++) {
                createOrb(iRow, iCol);
            }
        }

        Log.i("World", "Level is created.");
    }

    private OrbType generateOrbType() {
        // TODO: Подумать где дожна находится карта вероятностей выпадения. (GameRuler?)
        ArrayMap<OrbType, Float> orbTypeProbabilities = new ArrayMap<OrbType, Float>();
        orbTypeProbabilities.put(OrbType.TYPE1, 32f);
        orbTypeProbabilities.put(OrbType.TYPE2, 32f);
        orbTypeProbabilities.put(OrbType.TYPE3, 32f);
        orbTypeProbabilities.put(OrbType.UNIVERSAL, 4f);

        return GameMath.generateValue(orbTypeProbabilities);
    }

    private OrbSpecType generateOrbSpecType() {
        // TODO: Подумать где дожна находится карта вероятностей выпадения. (GameRuler?)
        ArrayMap<OrbSpecType, Float> orbTypeProbabilities = new ArrayMap<OrbSpecType, Float>();
        orbTypeProbabilities.put(OrbSpecType.NONE, 90f);
        orbTypeProbabilities.put(OrbSpecType.DOUBLE, 0f);
        orbTypeProbabilities.put(OrbSpecType.TRIPLE, 0f);
        orbTypeProbabilities.put(OrbSpecType.AROUND_EATER, 0f);
        orbTypeProbabilities.put(OrbSpecType.ROWS_EATER, 10f);
        orbTypeProbabilities.put(OrbSpecType.COLUMNS_EATER, 0f);

        return GameMath.generateValue(orbTypeProbabilities);
    }

    private void clearOrbs() {
        if (orbs_ != null)
        {
            for (int iRow = 0; iRow < nRows_; iRow++) {
                for (int iCol = 0; iCol < nColumns_; iCol++) {
                    orbs_[iRow][iCol].dispose();
                    orbs_[iRow][iCol] = null;
                }
            }
        }
    }

    private boolean hitOrb(Orb orb, Vector2 coords) {
        if (orb.hit(coords)) {
            if (!isOrbSelected(orb)) {
                // Если выбран до этого как минимум еще один.
                if (selectedOrbs_.size() > 0) {
                    Orb prevOrb = selectedOrbs_.get(selectedOrbs_.size() - 1);

                    // Если соседний, то выделяем (защита от нажатий несколькими пальцами в разных местах).
                    if ((Math.abs((orb.getColNo() - prevOrb.getColNo())) == 1) ||
                            (Math.abs((orb.getRowNo() - prevOrb.getRowNo())) == 1)) {
                        selectedOrbs_.add(orb);
                        TouchLine touchLine = new TouchLine(prevOrb, orb, touchLineTexture_);
                        touchLines_.add(touchLine);
                    }
                }
                else {
                    selectedOrbs_.add(orb);
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    private boolean isOrbSelected(Orb orb) {
        for (Orb selectedOrb : selectedOrbs_) {
            if (orb == selectedOrb) {
                return true;
            }
        }
        return false;
    }

    /**
     * Создать Orb.
     * @param rowNo Номер строки.
     * @param colNo Номер столбца.
     */
    private void createOrb(final int rowNo, final int colNo) {
        OrbType orbType = generateOrbType();
        OrbSpecType orbSpecType = generateOrbSpecType();
        createOrb(orbType, orbSpecType, rowNo, colNo);
    }

    /**
     * Создать Orb.
     * @param orbType Тип.
     * @param orbSpecType Специальный тип.
     * @param rowNo Номер строки.
     * @param colNo Номер столбца.
     */
    private void createOrb(final OrbType orbType, final OrbSpecType orbSpecType, final int rowNo, final int colNo) {
        Bitmap orbTexture = orbTextures_.get(orbType).get(orbSpecType);
        Vector2 orbPos = new Vector2(
                this.pos_.x + Orb.WIDTH * colNo,
                this.pos_.y + Orb.HEIGHT * rowNo);
        orbs_[rowNo][colNo] = new Orb(orbType, orbSpecType, orbPos, rowNo, colNo, orbTexture);
    }

    public void loadContent(GameView gameView) {
        int orbWidth = (int)(Orb.WIDTH * gameView.getScreenFactor());
        int orbHeight = (int)(Orb.HEIGHT * gameView.getScreenFactor());

        for (OrbType orbType : OrbType.values()) {
            for (OrbSpecType orbSpecType : OrbSpecType.values()) {

                int orbResourceId = Orb.getResourceId(orbType, orbSpecType);
                // Получаем и скейлим текстуру.
                Bitmap orbTexture = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(gameView.getResources(), orbResourceId),
                        Orb.WIDTH,
                        Orb.HEIGHT,
                        false);

                if (orbTextures_.get(orbType) != null) {
                    orbTextures_.get(orbType).put(orbSpecType, orbTexture);
                }
                else {
                    ArrayMap<OrbSpecType, Bitmap> orbSpecTypeTextures = new ArrayMap<OrbSpecType, Bitmap>();
                    orbSpecTypeTextures.put(orbSpecType, orbTexture);
                    orbTextures_.put(orbType, orbSpecTypeTextures);
                }
            }
        }

        // TODO: R.drawable.touch_line по аналогии с Orb.getResourceId() сделать.
        touchLineTexture_ = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(gameView.getResources(), R.drawable.touch_line),
                TouchLine.WIDTH,
                TouchLine.HEIGHT,
                false);
    }
}
