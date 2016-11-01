package com.blackteam.dsketches;

import android.util.Log;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

public class GameDot {
    public enum Types {
        TYPE1,
        TYPE2,
        TYPE3,
        TYPE4,
        UNIVERSAL
    }
    private Types type_;

    public enum SpecTypes {
        NONE, // Без эффекта.
        DOUBLE, // Удвоение profit.
        TRIPLE, // Утроение profit.
        ROW_EATER, // Разрушаются соседи по строке.
        COLUMN_EATER, // Разрушаются соседи по столбцу.
        AROUND_EATER // Разрушаются вокруг все соседи.
    }
    private SpecTypes specType_;

    private boolean isMoving;
    private boolean isFilmDevelopment;
    private Vector2 finishPos_;

    /** Количество очков, которое приносит игровая точка. */
    public static final int COST = 10;

    private static final float START_ALPHA_ = 0.0f;
    private static final float END_ALPHA_ = 1.0f;
    private static final float ALPHA_TIME_ = 500.0f; // ms. Время на изменения alpha-канала.
    private static final float ALPHA_SPEED_ = (END_ALPHA_ - START_ALPHA_) / ALPHA_TIME_; // units per ms.
    public static final float TRANSLATE_TIME_ = 200.0f; // ms. Время на перемещение.
    private static float ABS_TRANSLATE_SPEED_ = 1f / TRANSLATE_TIME_; // абсолютная ненаправленная, units per ms.

    private float curAlpha_;
    private Vector2 translateSpeed_ = new Vector2(0, 0); // units per ms.

    /** Ширина текстуры. */
    public static final int TEX_WIDTH = 256;
    /** Высота текстуры. */
    public static final int TEX_HEIGHT = 256;

    /** Главный объект, отображающий игровую точку. */
    private DisplayableObject mainObject_;
    /** Объект, отображающий специальность игровой точки. */
    private DisplayableObject specObject_;

    private int rowNo_;
    private int colNo_;

    public GameDot(final GameDot.Types dotType, final GameDot.SpecTypes dotSpecType, final Vector2 pos,
                   final int rowNo, final int colNo, final ContentManager contents) {

        this.type_ = dotType;
        this.specType_ = dotSpecType;
        this.rowNo_ = rowNo;
        this.colNo_ = colNo;

        curAlpha_ = START_ALPHA_;
        isMoving = false;
        isFilmDevelopment = true;

        TextureRegion textureRegion = new TextureRegion(
                contents.get(R.drawable.dots_theme1),
                getTexturePosition(dotType),
                new Size2(TEX_WIDTH, TEX_HEIGHT)
        );

        // TODO: Сделать конструктор DisplayableObject в которой подается сразу TextureRegion.
        mainObject_ = new DisplayableObject(pos, textureRegion.getTexture(),
                textureRegion.getPos().x, textureRegion.getPos().y,
                textureRegion.getSize().width, textureRegion.getSize().height
        );

        if (specType_ != SpecTypes.NONE) {
            TextureRegion specTextureRegion = new TextureRegion(
                    contents.get(R.drawable.dots_theme1),
                    getSpecTexturePosition(dotSpecType),
                    new Size2(TEX_WIDTH, TEX_HEIGHT)
            );

            specObject_ = new DisplayableObject(pos, specTextureRegion.getTexture(),
                    specTextureRegion.getPos().x, specTextureRegion.getPos().y,
                    specTextureRegion.getSize().width, specTextureRegion.getSize().height
            );
        }
    }

    public int getColNo() {
        return colNo_;
    }

    public void setColNo(final int colNo) {
        colNo_ = colNo;
    }

    public int getRowNo() {
        return rowNo_;
    }

    public void setRowNo(final int rowNo) {
        rowNo_ = rowNo;
    }

    public float getX() {
        return mainObject_.getX();
    }

    public float getY() {
        return mainObject_.getY();
    }

    public Vector2 getPosition() {return  mainObject_.getPosition(); }

    public float getWidth() {
        return mainObject_.getWidth();
    }

    public float getHeight() {
        return mainObject_.getHeight();
    }

    public GameDot.Types getType() {
        return this.type_;
    }

    public void setType(GameDot.Types dotType) {
        this.type_ = dotType;
    }

    public GameDot.SpecTypes getSpecType() {
        return this.specType_;
    }

    public static Vector2 getTexturePosition(GameDot.Types type) {
        int x = 0;

        switch (type) {
            case TYPE1:
                x = 0;
                break;
            case TYPE2:
                x = GameDot.TEX_HEIGHT;
                break;
            case TYPE3:
                x = 2 * GameDot.TEX_HEIGHT;
                break;
            case TYPE4:
                x = 3 * GameDot.TEX_HEIGHT;
                break;
            case UNIVERSAL:
                x = 4 * GameDot.TEX_HEIGHT;
                break;
        }

        return new Vector2(x, 0);
    }

    public static Vector2 getSpecTexturePosition(GameDot.SpecTypes specType) {
        int x = 0;

        switch (specType) {
            case NONE:
                x = 0; // Берём этот, но на деле мы ничего не отрисовываем.
                break;
            case DOUBLE:
                x = 0;
                break;
            case TRIPLE:
                x = GameDot.TEX_WIDTH;
                break;
            case ROW_EATER:
                x = 2 * GameDot.TEX_WIDTH;
                break;
            case COLUMN_EATER:
                x = 3 * GameDot.TEX_WIDTH;
                break;
            case AROUND_EATER:
                x = 4 * GameDot.TEX_WIDTH;
                break;
        }

        return new Vector2(x, TEX_HEIGHT);
    }

    public static void setAbsTranslateSpeed(final float speed) {
        ABS_TRANSLATE_SPEED_ = speed;
    }

    public void setSize(float size) {
        mainObject_.setSize(size, size);
        if (specType_ != SpecTypes.NONE)
            specObject_.setSize(size, size);
    }

    public void setSizeCenter(float size) {
        mainObject_.setSizeCenter(size, size);
        if (specType_ != SpecTypes.NONE)
            specObject_.setSizeCenter(size, size);
    }

    public void setPosition(Vector2 dotPos) {
        mainObject_.setPosition(dotPos);
        if (specType_ != SpecTypes.NONE)
            specObject_.setPosition(dotPos);
    }

    public void setAlpha(float alphaFactor) {
        mainObject_.setAlpha(alphaFactor);
        if (specType_ != SpecTypes.NONE)
            specObject_.setAlpha(alphaFactor);
    }

    public static Types convertToType(String gameDotTypeStr) {
        return Enum.valueOf(GameDot.Types.class, gameDotTypeStr);
    }

    public static SpecTypes convertToSpecType(String gameDotSpecTypeStr) {
        return Enum.valueOf(GameDot.SpecTypes.class, gameDotSpecTypeStr);
    }

    public void draw(float[] mvpMatrix, ShaderProgram shader, float elapsedTime) {
        if (isMoving)
            moving(elapsedTime);
        if (isFilmDevelopment)
            filmDevelopment(elapsedTime);

        mainObject_.draw(mvpMatrix, shader);
        if (specType_ != SpecTypes.NONE)
            specObject_.draw(mvpMatrix, shader);
    }

    public boolean hit(Vector2 coords) {
        return mainObject_.hit(coords);
    }

    public boolean isIdenticalType(GameDot.Types dotType) {
        if ((type_ == dotType) ||
                (type_ == GameDot.Types.UNIVERSAL) || (dotType == GameDot.Types.UNIVERSAL)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void moveTo(Vector2 finishPos) {
        finishPos_ = finishPos;

        // Определяем скорость направленную.
        if (finishPos_.x - getPosition().x > 0)
            translateSpeed_.x = ABS_TRANSLATE_SPEED_;
        else if (finishPos_.x - getPosition().x < 0)
            translateSpeed_.x = -ABS_TRANSLATE_SPEED_;
        else
            translateSpeed_.x = 0.0f;

        if (finishPos_.y - getPosition().y > 0)
            translateSpeed_.y = ABS_TRANSLATE_SPEED_;
        else if (finishPos_.y - getPosition().y < 0)
            translateSpeed_.y = -ABS_TRANSLATE_SPEED_;
        else
            translateSpeed_.y = 0.0f;

        //if (BuildConfig.DEBUG) {
        //    Log.i("GameDot", String.format("(%d, %d): pos = {%f, %f}; finish = {%f, %f}; speed = {%f, %f}.",
        //            rowNo_, colNo_, getPosition().x, getPosition().y, finishPos_.x, finishPos_.y, translateSpeed_.x, translateSpeed_.y));
        //}

        isMoving = true;
    }

    private void moving(final float elapsedTime) {
        Vector2 distance = new Vector2(0, 0);
        boolean isMovedX = ((translateSpeed_.x > 0) && (getPosition().x < finishPos_.x)) ||
                ((translateSpeed_.x < 0) && (getPosition().x > finishPos_.x));
        if (isMovedX) {
            distance.x = translateSpeed_.x * elapsedTime;
        }
        else {
            distance.x = finishPos_.x - getPosition().x;
        }

        boolean isMovedY = ((translateSpeed_.y > 0) && (getPosition().y < finishPos_.y)) ||
                ((translateSpeed_.y < 0) && (getPosition().y > finishPos_.y));
        if (isMovedY) {
            distance.y = translateSpeed_.y * elapsedTime;
        }
        else {
            distance.y = finishPos_.y - getPosition().y;
        }

        //if (BuildConfig.DEBUG) {
        //    Log.i("GameDot", String.format("(%d, %d): pos = {%f, %f} translates to (%f, %f); distance = (%f, %f); translateSpeed = (%f, %f); elapsedTime = %f",
        //            rowNo_, colNo_, getPosition().x, getPosition().y, finishPos_.x, finishPos_.y, distance.x, distance.y, translateSpeed_.x, translateSpeed_.y, elapsedTime));
        //}

        mainObject_.addPosition(distance);
        if (specType_ != SpecTypes.NONE)
            specObject_.addPosition(distance);

        isMoving = isMovedX || isMovedY;
    }

    private void filmDevelopment(final float elapsedTime) {
        curAlpha_ += ALPHA_SPEED_ * elapsedTime;
        if (curAlpha_ < END_ALPHA_) {
            setAlpha(curAlpha_);
        }
        else {
            setAlpha(END_ALPHA_);
            isFilmDevelopment = false;
        }
    }
}