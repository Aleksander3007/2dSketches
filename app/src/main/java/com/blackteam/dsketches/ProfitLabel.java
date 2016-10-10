package com.blackteam.dsketches;

import android.content.Context;
import com.blackteam.dsketches.Utils.NumberLabel;
import com.blackteam.dsketches.Utils.Vector2;

/**
 * Количество очков полученное с текущего действия.
 */
public class ProfitLabel extends NumberLabel {
    private static final float START_ALPHA_ = 1.0f;
    private static final float END_ALPHA_ = 0.85f;
    private static final float ALPHA_SPEED_ = -0.0014f; // units per ms.
    private static final float TRANSLATE_SPEED_ = 0.0006f; // units per ms.

    private float curAlpha_;

    private boolean isVisible_ = true;

    public ProfitLabel(final Texture texture) {
        super(texture);
    }

    public void setProfit(int val, Vector2 pos) {
        curAlpha_ = START_ALPHA_;
        setValue(val);
        setPosition(calculatePosition(pos));
        isVisible_ = true;
    }

    private Vector2 calculatePosition(Vector2 pos) {
        float numberSize = digits_.size() * digitWidth_;
        return new Vector2(pos.x - numberSize / 2f, pos.y);
    }

    public void render(float[] mvpMatrix, final ShaderProgram shader, float elapsedTime) {
        if (isVisible_) {
            curAlpha_ += ALPHA_SPEED_ * elapsedTime;
            for (DisplayableObject number : digits_) {
                if (Math.abs(curAlpha_ - END_ALPHA_) > 0) {
                    number.setAlpha(curAlpha_);
                    number.addPosition(new Vector2(0, TRANSLATE_SPEED_ * elapsedTime));
                } else {
                    isVisible_ = false;
                }
                number.draw(mvpMatrix, shader);
            }
        }
    }
}
