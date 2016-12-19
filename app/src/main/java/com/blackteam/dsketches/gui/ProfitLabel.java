package com.blackteam.dsketches.gui;

import com.blackteam.dsketches.animation.AnimationController;
import com.blackteam.dsketches.animation.AnimationSet;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Количество очков полученное с текущего действия.
 */
public class ProfitLabel extends NumberLabel {
    private static final float TRANSLATE_SPEED_ = 0.0006f; // units per ms.

    /** Параметры для эффекта плавного исчезновения цифр. */
    private static final float START_ALPHA_ = 1.0f;
    private static final float END_ALPHA_ = 0.5f;
    private static final float ALPHA_TIME_ = 500.0f; // Время на изменения alpha-канала, ms.
    private static final AnimationSet DISAPPEARING_ANIM_SET_ = new AnimationSet(AnimationSet.ValueType.ALPHA,
            AnimationSet.PlayMode.NORMAL,
            START_ALPHA_, END_ALPHA_, ALPHA_TIME_);

    private boolean isVisible_ = true;

    public ProfitLabel(final Texture texture) {
        super(texture);
    }

    public void setProfit(int val, Vector2 pos) {
        setValue(val);
        setPosition(calculatePosition(pos));
        isVisible_ = true;
        for (DisplayableObject number : mDigits) {
            number.setAnimation(new AnimationController(DISAPPEARING_ANIM_SET_));
        }
    }

    private Vector2 calculatePosition(Vector2 pos) {
        float numberSize = mDigits.size() * mDigitWidth;
        return new Vector2(pos.x - numberSize / 2f, pos.y);
    }

    public void render(Graphics graphics) {
        if (isVisible_) {
            for (DisplayableObject number : mDigits) {
                if (!number.isAnimationFinished()) {
                    number.addPosition(new Vector2(0, TRANSLATE_SPEED_ * graphics.getElapsedTime()));
                    number.render(graphics);
                }
            }
        }
    }
}
