package com.blackteam.dsketches.animation;

import android.util.Log;

/**
 * Хранение данных для анимации объекта.
 */
public class AnimationSet {
    /** Тип трансформации. */
    public enum ValueType {
        TRANSLATE,
        ROTATE,
        SCALE_X,
        SCALE_Y,
        SCALE,
        ALPHA
    }
    private ValueType valueType_;

    /** Режим проигрывания анимации. */
    public enum PlayMode {
        NORMAL,
        LOOP,
        LOOP_PINGPONG
    }
    private PlayMode playMode_ = PlayMode.NORMAL;

    private float minVal_;
    private float maxVal_;
    private float speed_;
    private float delayedStart_;

    private float lastVal_;
    private boolean isForward_;
    private boolean isFinished_;

    /**
     * Конструктор.
     * @param valType Тип трансформации.
     * @param playMode Режим проигрывания анимации.
     * @param minVal Минимальная величина.
     * @param maxVal Максимальная величина.
     * @param speed Скорость изменения величины.
     */
    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final float minVal, final float maxVal,
                        final float speed) {
        this(valType, playMode, minVal, maxVal, speed, 0.0f);
    }

    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final float minVal, final float maxVal,
                        final float speed, final float delayedStart) {
        this.valueType_ = valType;
        this.playMode_ = playMode;
        this.minVal_ = minVal;
        this.maxVal_ = maxVal;
        this.speed_ = speed;
        this.delayedStart_ = delayedStart;

        this.isForward_ = true;
        this.lastVal_ = getMinVal();
        isFinished_ = false;
    }

    public AnimationSet(AnimationSet animationSet) {
        this(animationSet.getValueType(), animationSet.getPlayMode(),
                animationSet.getMinVal(), animationSet.getMaxVal(), animationSet.getSpeed(),
                animationSet.getDelayedStart());
    }

    public PlayMode getPlayMode() {
        return playMode_;
    }

    public float getMinVal() {
        return minVal_;
    }

    public float getMaxVal() {
        return maxVal_;
    }

    public float getSpeed() {
        return speed_;
    }

    public float getDelayedStart() {
        return delayedStart_;
    }

    public float getValue() {
        return lastVal_;
    }

    public AnimationSet.ValueType getValueType() {
        return valueType_;
    }

    public void update(final float elapsedTime) {
        float value = lastVal_ + getSpeed() * elapsedTime;
        boolean overflow = (isForward_ && (value >= getMaxVal())) ||
                (!isForward_ && (value <= getMinVal()));

        if (overflow) {
            switch (getPlayMode()) {
                case NORMAL:
                    value = getMaxVal();
                    isFinished_ = true;
                    break;
                case LOOP:
                    value = getMinVal();
                    break;
                case LOOP_PINGPONG:
                    speed_ = -getSpeed();
                    isForward_ = !isForward_;
                    break;
            }
        }

        lastVal_ = value;
    }

    public boolean isFinished() {
        return isFinished_;
    }
}
