package com.blackteam.dsketches.animation;

import com.blackteam.dsketches.utils.GameMath;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Хранение данных для анимации объекта.
 * Выбранный тип трансформации меняется от startVal до endVal со скоростью speed.
 */
public class AnimationSet {
    /** Тип трансформации. */
    public enum ValueType {
        TRANSLATE,
        ROTATE,
        SCALE,
        SCALE_CENTER,
        ALPHA
    }
    private ValueType mValueType;

    /** Режим проигрывания анимации. */
    public enum PlayMode {
        NORMAL,
        LOOP,
        LOOP_PINGPONG
    }
    private PlayMode mPlayMode = PlayMode.NORMAL;

    private Vector2 mStartVal;
    private Vector2 mEndVal;
    private float mSpeed;
    private byte mSpeedDirectionX;
    private byte mSpeedDirectionY;
    private float mDuration;
    private float mDelayedStart;

    private float mLastVal;
    private float mDistance;
    /** Угол движения. */
    private float mAngle;
    private boolean mIsForward;
    private boolean mIsFinished;

    /**
     * Конструктор.
     * @param valType Тип трансформации.
     * @param playMode Режим проигрывания анимации.
     * @param startVal Начальная величина.
     * @param endVal Конечная величина.
     * @param duration Длительность анимации.
     */
    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final float startVal, final float endVal,
                        final float duration) {
        // Одиночнуб величину мы записываем в x-значение вектора.
        this(valType, playMode,
                new Vector2(startVal, 0.0f), new Vector2(endVal, 0.0f),
                duration, 0f);
    }
    /**
     * Конструктор.
     * @param valType Тип трансформации.
     * @param playMode Режим проигрывания анимации.
     * @param startVal Минимальная величина.
     * @param endVal Максимальная величина.
     * @param duration Длительность анимации.
     */
    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final Vector2 startVal, final Vector2 endVal,
                        final float duration) {
        this(valType, playMode, startVal, endVal, duration, 0f);
    }

    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final Vector2 startVal, final Vector2 endVal,
                        final float duration, final float delayedStart) {
        this.mValueType = valType;
        this.mPlayMode = playMode;
        this.mStartVal = new Vector2(startVal);
        this.mEndVal = new Vector2(endVal);
        this.mDuration = duration;
        this.mDelayedStart = delayedStart;

        Vector2 distanceVector = GameMath.sub(mEndVal, mStartVal);
        mDistance = (float) Math.sqrt(distanceVector.x * distanceVector.x + distanceVector.y * distanceVector.y);
        mAngle = (float) Math.atan2(distanceVector.y, distanceVector.x);

        this.mSpeed = mDistance / duration;
        mSpeedDirectionX = calculateSpeedDirection(mStartVal.x, mEndVal.x);
        mSpeedDirectionY = calculateSpeedDirection(mStartVal.y, mEndVal.y);

        this.mIsForward = true;
        this.mLastVal = 0.0f;
        mIsFinished = false;
    }

    public AnimationSet(AnimationSet animationSet) {
        this(animationSet.getValueType(), animationSet.getPlayMode(),
                animationSet.getStartVal(), animationSet.getEndVal(), animationSet.getDuration(),
                animationSet.getDelayedStart());
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public Vector2 getStartVal() { return mStartVal; }

    public Vector2 getEndVal() { return mEndVal; }

    public float getDuration() {
        return mDuration;
    }

    public float getDelayedStart() {
        return mDelayedStart;
    }

    public float getValue() {
        // Если задавалась одна величина (не вектор), то она записалась в x.
        return (mStartVal.x + mSpeedDirectionX * mLastVal);
    }

    public Vector2 getValues() {
        Vector2 absDist = new Vector2(mSpeedDirectionX * mLastVal * (float)Math.cos(mAngle),
                mSpeedDirectionY * mLastVal * (float)Math.sin(mAngle)
        );

        return GameMath.add(mStartVal, absDist);
    }

    public AnimationSet.ValueType getValueType() {
        return mValueType;
    }

    public void update(final float elapsedTime) {
        float value = mLastVal + mSpeed * elapsedTime;

        boolean overflow = (mIsForward && (value >= mDistance)) ||
                (!mIsForward && (value <= 0.0f));

        if (overflow) {
            switch (getPlayMode()) {
                case NORMAL:
                    value = mDistance;
                    mIsFinished = true;
                    break;
                case LOOP:
                    value = 0.0f;
                    break;
                case LOOP_PINGPONG:
                    mSpeed = -mSpeed;
                    mIsForward = !mIsForward;
                    break;
            }
        }

        mLastVal = value;
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    private byte calculateSpeedDirection(final float startVal, final float endVal) {
        if (endVal > startVal) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
