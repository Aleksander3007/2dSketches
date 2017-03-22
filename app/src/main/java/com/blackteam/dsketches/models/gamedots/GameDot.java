package com.blackteam.dsketches.models.gamedots;

import com.blackteam.dsketches.GameDotsFactory;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.animation.AnimationController;
import com.blackteam.dsketches.animation.AnimationSet;
import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.Set;

/**
 * Игровая точка без дополнительных эффектов, основная игровая единица.
 */
public class GameDot {
    public enum Types {
        TYPE1,
        TYPE2,
        TYPE3,
        TYPE4,
        UNIVERSAL
    }
    private Types mType;

    public enum SpecTypes {
        NONE, // Без эффекта.
        DOUBLE, // Удвоение profit.
        TRIPLE, // Утроение profit.
        ROW_EATER, // Разрушаются соседи по строке.
        COLUMN_EATER, // Разрушаются соседи по столбцу.
        AROUND_EATER // Разрушаются вокруг все соседи.
    }
    private SpecTypes mSpecType;

    private boolean mIsMoving = false;
    private Vector2 mFinishPos;

    /** Количество очков, которое приносит игровая точка. */
    public static final int COST = 10;

    /** Параметры для эффекта плавного появления игровых точек. */
    private static final float MIN_ALPHA_ = 0.0f;
    private static final float MAX_ALPHA_ = 1.0f;
    private static final float ALPHA_TIME_ = 500.0f; // Время на изменения alpha-канала, ms.
    protected static final AnimationSet FILM_DEVELOPMENT_ANIM_SET_ = new AnimationSet(AnimationSet.ValueType.ALPHA,
            AnimationSet.PlayMode.NORMAL,
            MIN_ALPHA_, MAX_ALPHA_, ALPHA_TIME_);

    /** Время отображения эффекта, мс. */
    private static final float EFFECT_TIME_ = 300f;

    /** Время на перемещение, мс. */
    public static final float TRANSLATE_TIME_ = 200.0f;
    /** Абсолютная ненаправленная скорость, units per ms. */
    private static float ABS_TRANSLATE_SPEED_ = 1f / TRANSLATE_TIME_;

    private Vector2 translateSpeed_ = new Vector2(0, 0); // units per ms.

    /** Главный объект, отображающий игровую точку. */
    protected DisplayableObject mainObject_;

    protected int rowNo_;
    protected int colNo_;

    protected ContentManager mContents;

    public GameDot(final GameDot.Types dotType, final GameDot.SpecTypes dotSpecType, final Vector2 pos,
                   final int rowNo, final int colNo, final ContentManager contents) {

        this.mContents = contents;
        this.mType = dotType;
        this.mSpecType = dotSpecType;
        this.rowNo_ = rowNo;
        this.colNo_ = colNo;

        mainObject_ = new DisplayableObject(pos, GameDotsFactory.getTextureRegion(mType, mContents));
        mainObject_.setAnimation(new AnimationController(FILM_DEVELOPMENT_ANIM_SET_));
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
        return this.mType;
    }

    public void setType(GameDot.Types dotType) {
        this.mType = dotType;
        mainObject_.setTexture(GameDotsFactory.getTextureRegion(dotType, mContents));
    }

    public GameDot.SpecTypes getSpecType() {
        return this.mSpecType;
    }

    public static void setAbsTranslateSpeed(final float speed) {
        ABS_TRANSLATE_SPEED_ = speed;
    }

    public void setSize(float size) {
        mainObject_.setSize(size, size);
    }

    public void setSizeCenter(float size) {
        mainObject_.setSizeCenter(size, size);
    }

    public void setPosition(Vector2 dotPos) {
        mainObject_.setPosition(dotPos);
    }

    public void setAlpha(float alphaFactor) {
        mainObject_.setAlpha(alphaFactor);
    }

    public static Types convertToType(String gameDotTypeStr) {
        return Enum.valueOf(GameDot.Types.class, gameDotTypeStr);
    }

    public static SpecTypes convertToSpecType(String gameDotSpecTypeStr) {
        return Enum.valueOf(GameDot.SpecTypes.class, gameDotSpecTypeStr);
    }

    public void startCreatingAnimation() {
        mainObject_.setAnimation(new AnimationController(FILM_DEVELOPMENT_ANIM_SET_));
    }

    /**
     * Главный метод отрисовки.
     */
    public void render(Graphics graphics) {
        if (mIsMoving)
            moving(graphics.getElapsedTime());

        mainObject_.render(graphics);
    }

    public boolean hit(Vector2 coords) {
        return mainObject_.hit(coords);
    }

    public boolean isIdenticalType(GameDot.Types dotType) {
        return (mType == dotType) ||
                (mType == Types.UNIVERSAL) || (dotType == Types.UNIVERSAL);
    }

    public void moveTo(Vector2 finishPos) {
        mFinishPos = finishPos;

        // Определяем скорость направленную.
        if (mFinishPos.x - getPosition().x > 0)
            translateSpeed_.x = ABS_TRANSLATE_SPEED_;
        else if (mFinishPos.x - getPosition().x < 0)
            translateSpeed_.x = -ABS_TRANSLATE_SPEED_;
        else
            translateSpeed_.x = 0.0f;

        if (mFinishPos.y - getPosition().y > 0)
            translateSpeed_.y = ABS_TRANSLATE_SPEED_;
        else if (mFinishPos.y - getPosition().y < 0)
            translateSpeed_.y = -ABS_TRANSLATE_SPEED_;
        else
            translateSpeed_.y = 0.0f;

        //if (BuildConfig.DEBUG) {
        //    Log.i("GameDot", String.format("(%d, %d): pos = {%f, %f}; finish = {%f, %f}; speed = {%f, %f}.",
        //            rowNo_, colNo_, getPosition().x, getPosition().y, mFinishPos.x, mFinishPos.y, translateSpeed_.x, translateSpeed_.y));
        //}

        mIsMoving = true;
    }

    protected void moving(final float elapsedTime) {
        Vector2 distance = new Vector2(0, 0);
        boolean isMovedX = ((translateSpeed_.x > 0) && (getPosition().x < mFinishPos.x)) ||
                ((translateSpeed_.x < 0) && (getPosition().x > mFinishPos.x));
        if (isMovedX) {
            distance.x = translateSpeed_.x * elapsedTime;
        }
        else {
            distance.x = mFinishPos.x - getPosition().x;
        }

        boolean isMovedY = ((translateSpeed_.y > 0) && (getPosition().y < mFinishPos.y)) ||
                ((translateSpeed_.y < 0) && (getPosition().y > mFinishPos.y));
        if (isMovedY) {
            distance.y = translateSpeed_.y * elapsedTime;
        }
        else {
            distance.y = mFinishPos.y - getPosition().y;
        }

        //if (BuildConfig.DEBUG) {
        //    Log.i("GameDot", String.format("(%d, %d): pos = {%f, %f} translates to (%f, %f); distance = (%f, %f); translateSpeed = (%f, %f); elapsedTime = %f",
        //            rowNo_, colNo_, getPosition().x, getPosition().y, mFinishPos.x, mFinishPos.y, distance.x, distance.y, translateSpeed_.x, translateSpeed_.y, elapsedTime));
        //}

        mainObject_.addPosition(distance);

        mIsMoving = isMovedX || isMovedY;
    }

    /**
     * Оказать воздействие на фактор (множитель profit-а).
     * @param originalFactor Начальное значение фактора.
     * @return итоговое значение фактора.
     */
    public int affectFactor(int originalFactor) {
        return originalFactor;
    }

    /**
     * Оказать воздействие на другие игровые точки.
     * @param gameDots список точек на которые может быть оказано влияние.
     */
    public Set<GameDot> affectDots(GameDot[][] gameDots) {
        // обычная игровая точка не оказывает никакого влияния на другие точки.
        return null;
    }

    /**
     * Получить анимацию разрушения объекта.
     * @param dotSize размер игровой точки.
     * @param boxSize размер игрового поля.
     * @return объект анимации.
     */
    public DisplayableObject getDestroyAnimation(Size2 dotSize, Size2 boxSize) {
        return null;
    }

    /**
     * Создание анимации изменения масштаба объекта.
     * @param startDotSize начальный размер игровой точки.
     * @param endDotSize конечный размер игровой точки.
     * @return объект анимации.
     */
    public DisplayableObject createScaleAnimation(Size2 startDotSize, Size2 endDotSize) {

        TextureRegion textureRegion = GameDotsFactory.getSpecTextureRegion(getSpecType(), mContents);

        DisplayableObject effect = new DisplayableObject(textureRegion);
        effect.setSize(startDotSize);
        effect.setPosition(getPosition());

        AnimationSet animSet = new AnimationSet(AnimationSet.ValueType.SCALE_CENTER,
                AnimationSet.PlayMode.NORMAL,
                new Vector2(startDotSize.width, startDotSize.height),
                new Vector2(endDotSize.width, endDotSize.height),
                EFFECT_TIME_);
        effect.setAnimation(new AnimationController(animSet));

        return effect;
    }
}
