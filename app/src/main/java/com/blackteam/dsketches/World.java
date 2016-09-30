package com.blackteam.dsketches;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;

/**
 * Модель мира.
 */
public class World {
    private final ShaderProgram shader_;
    private Texture touchLineTexture_;
    private final ArrayMap<OrbType, ArrayMap<OrbSpecType, Texture>> orbTextures_ = new ArrayMap<>();

    private Vector2 pos_;
    private float width_;
    private float height_;
    private int nRows_;
    private int nColumns_;
    private Orb[][] orbs_;
    private ArrayList<Orb> selectedOrbs_ = new ArrayList<>();
    private ArrayList<TouchLine> touchLines_ = new ArrayList<>();

    private float orbSize_;
    private float touchLineWidth_;
    private float touchLineHeight_;

    public World(final Vector2 pos, final Size2 rectSize,
                 ShaderProgram shader) {
        shader_ = shader;

        this.nRows_ = 12; // TODO: Magic number!
        this.nColumns_ = 7; // TODO: Magic number!

        float orbHeight = rectSize.height / nRows_;
        float orbWidth = rectSize.width / nColumns_;

        orbSize_ = (orbWidth < orbHeight) ? orbWidth : orbHeight;

        this.height_ = orbSize_ * nRows_;
        this.width_ = orbSize_ * nColumns_;
        this.pos_ = new Vector2(pos.x + (rectSize.width / 2) - (this.width_ / 2), pos.y);

        touchLineWidth_ = orbSize_; /* (Orb.WIDTH / 2) + (Orb.WIDTH / 2) */
        touchLineHeight_ = orbSize_ / 4;
    }

    public void init() {
        createLevel();
    }

    public void onDraw(float[] mvpMatrix) {
        for (int iRow = 0; iRow < nRows_; iRow++) {
            for (int iCol = 0; iCol < nColumns_; iCol++) {
                orbs_[iRow][iCol].draw(mvpMatrix);
            }
        }
        for (TouchLine touchLine : touchLines_) {
            touchLine.draw(mvpMatrix);
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
        ArrayMap<OrbType, Float> orbTypeProbabilities = new ArrayMap<>();
        orbTypeProbabilities.put(OrbType.TYPE1, 32f);
        orbTypeProbabilities.put(OrbType.TYPE2, 32f);
        orbTypeProbabilities.put(OrbType.TYPE3, 32f);
        orbTypeProbabilities.put(OrbType.UNIVERSAL, 4f);

        return GameMath.generateValue(orbTypeProbabilities);
    }

    private OrbSpecType generateOrbSpecType() {
        // TODO: Подумать где дожна находится карта вероятностей выпадения. (GameRuler?)
        ArrayMap<OrbSpecType, Float> orbTypeProbabilities = new ArrayMap<>();
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
                        TouchLine touchLine = new TouchLine(prevOrb, orb, touchLineTexture_, shader_);
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
    private void createOrb(final OrbType orbType, final OrbSpecType orbSpecType,
                           final int rowNo, final int colNo
    ) {
        Texture orbTexture = orbTextures_.get(orbType).get(orbSpecType);
        Vector2 orbPos = new Vector2(
                this.pos_.x + colNo * orbSize_,
                this.pos_.y + rowNo * orbSize_);
        orbs_[rowNo][colNo] = new Orb(orbType, orbSpecType,
                orbPos,
                rowNo, colNo,
                orbTexture, shader_
        );

        orbs_[rowNo][colNo].setSize(orbSize_);
    }

    // Тут по хорошему нужен аналог AssetsManager из libgdx (грузится в одном месте, а получать в другом).
    public void loadContent(Context context) {
        for (OrbType orbType : OrbType.values()) {
            for (OrbSpecType orbSpecType : OrbSpecType.values()) {

                int orbResourceId = Orb.getResourceId(orbType, orbSpecType);
                Texture orbTexture = new Texture(context, orbResourceId);

                if (orbTextures_.get(orbType) != null) {
                    orbTextures_.get(orbType).put(orbSpecType, orbTexture);
                }
                else {
                    ArrayMap<OrbSpecType, Texture> orbSpecTypeTextures = new ArrayMap<>();
                    orbSpecTypeTextures.put(orbSpecType, orbTexture);
                    orbTextures_.put(orbType, orbSpecTypeTextures);
                }
            }
        }
        Log.i("World.Content", "Content of world are loaded.");
        // TODO: R.drawable.touch_line по аналогии с Orb.getResourceId() сделать.
        //int orbResourceId = Orb.getResourceId(orbType, orbSpecType);
        //Texture texture = new Texture(context, TouchLine.resourceId);
    }
}
