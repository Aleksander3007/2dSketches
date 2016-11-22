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
    private ValueType valueType_;

    /** Режим проигрывания анимации. */
    public enum PlayMode {
        NORMAL,
        LOOP,
        LOOP_PINGPONG
    }
    private PlayMode playMode_ = PlayMode.NORMAL;

    private Vector2 startVal_;
    private Vector2 endVal_;
    private float speed_;
    private float delayedStart_;

    private float lastVal_;
    private float distance_;
    private float angle_; // угол движения.
    private boolean isForward_;
    private boolean isFinished_;

    /**
     * Конструктор.
     * @param valType Тип трансформации.
     * @param playMode Режим проигрывания анимации.
     * @param startVal Начальная величина.
     * @param endVal Конечная величина.
     * @param speed Скорость изменения величины.
     */
    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final float startVal, final float endVal,
                        final float speed) {
        this(valType, playMode,
                new Vector2(startVal, startVal), new Vector2(endVal, endVal),
                speed, 0f);
    }
    /**
     * Конструктор.
     * @param valType Тип трансформации.
     * @param playMode Режим проигрывания анимации.
     * @param startVal Минимальная величина.
     * @param endVal Максимальная величина.
     * @param speed Скорость изменения величины.
     */
    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final Vector2 startVal, final Vector2 endVal,
                        final float speed) {
        this(valType, playMode, startVal, endVal, speed, 0f);
    }

    public AnimationSet(final AnimationSet.ValueType valType,
                        final PlayMode playMode,
                        final Vector2 startVal, final Vector2 endVal,
                        final float speed, final float delayedStart) {
        this.valueType_ = valType;
        this.playMode_ = playMode;
        this.startVal_ = new Vector2(startVal);
        this.endVal_ = new Vector2(endVal);
        this.speed_ = speed;
        this.delayedStart_ = delayedStart;

        Vector2 distanceVector = GameMath.sub(endVal_, startVal_);
        distance_ = (float) Math.sqrt(distanceVector.x * distanceVector.x + distanceVector.y * distanceVector.y);
        angle_ = (float) Math.atan2(distanceVector.y, distanceVector.x);

        this.isForward_ = true;
        this.lastVal_ = 0.0f;
        isFinished_ = false;
    }

    public AnimationSet(AnimationSet animationSet) {
        this(animationSet.getValueType(), animationSet.getPlayMode(),
                animationSet.getStartVal(), animationSet.getEndVal(), animationSet.getSpeed(),
                animationSet.getDelayedStart());
    }

    public PlayMode getPlayMode() {
        return playMode_;
    }

    public Vector2 getStartVal() { return startVal_; }

    public Vector2 getEndVal() { return endVal_; }

    public float getSpeed() {
        return speed_;
    }

    public float getDelayedStart() {
        return delayedStart_;
    }

    public float getValue() {return lastVal_; }

    public Vector2 getValue2() {
        Vector2 absDist = new Vector2(lastVal_ * (float)Math.cos(angle_),
                lastVal_ * (float)Math.sin(angle_)
        );
        return GameMath.add(startVal_, absDist);
    }

    public AnimationSet.ValueType getValueType() {
        return valueType_;
    }

    public void update(final float elapsedTime) {
        float value = lastVal_ + getSpeed() * elapsedTime;

        boolean overflow = (isForward_ && (value >= distance_)) ||
                (!isForward_ && (value <= 0.0f));

        if (overflow) {
            switch (getPlayMode()) {
                case NORMAL:
                    value = distance_;
                    isFinished_ = true;
                    break;
                case LOOP:
                    value = 0.0f;
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
