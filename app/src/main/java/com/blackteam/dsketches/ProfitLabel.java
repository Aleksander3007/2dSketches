package com.blackteam.dsketches;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.view.animation.Animation;

import java.util.ArrayList;

/**
 * Количество очков полученное с текущего действия.
 */
public class ProfitLabel {
    private Texture numbersTexture_;

    private Vector2 pos_;
    private int value_;
    private ArrayList<ScoreNumber> numbers_ = new ArrayList<>();
    private float numberHeight_;
    private float numberWidth_;

    private float startScale_;
    private float endScale_;
    private float curScale_;
    private float scaleSpeed_; // units per ms.
    private float translateSpeed_; // units per ms.

    private boolean isVisible_;

    public ProfitLabel(final Context context) {
        loadContent(context);
    }

    public void init(final Size2 rectSize) {
        numberHeight_ = rectSize.height;
        numberWidth_ = rectSize.height;

        startScale_ = 1.0f;
        endScale_ = 0.7f;
        scaleSpeed_ = -0.0019f;
        translateSpeed_ = 0.0006f;
    }

    public void setScore(int val, Vector2 pos) {
        value_ = val;
        pos_ = pos;

        numbers_.clear();

        Log.i("ProfitLabel.setScore()", String.valueOf(value_));

        if (value_ == 0) {
            // TODO: Текстуру должен знать ScoreNumber.
            // TODO: пока используем ScoreNumber.
            ScoreNumber scoreNumber = new ScoreNumber(
                    new Vector2(this.pos_.x, this.pos_.y),
                    numbersTexture_, 0, 0, 32, 32);
            scoreNumber.setSize(numberWidth_, numberHeight_);
            numbers_.add(scoreNumber);
        }

        // Выделяем цифры из числа (и записываем в массив).
        int rest = value_;
        while (rest >= 1) {
            int number = rest % 10;
            rest = rest / 10;

            ScoreNumber scoreNumber = new ScoreNumber(
                    new Vector2(this.pos_.x, this.pos_.y), // Правильная позиция устанавливается далее.
                    numbersTexture_, number * 32, 0, 32, 32);
            scoreNumber.setSize(numberWidth_, numberHeight_);
            numbers_.add(scoreNumber);
        }

        // Устанавливаем позицию.
        for (int iNumber = 0; iNumber < numbers_.size(); iNumber++) {
            numbers_.get(numbers_.size() - iNumber - 1).setPosition(
                    this.pos_.x + iNumber * numberWidth_,
                    this.pos_.y
            );
        }

        curScale_ = startScale_;
        for (ScoreNumber number : numbers_) {
            number.addPosition(new Vector2(-2 * numberWidth_, 0));
        }
        isVisible_ = true;
    }

    // TODO: В ScoreLabel используется та же текстуры, дублирование!!
    // По идеи должен быть AssetsManager, как в libgdx.
    public void loadContent(Context context) {
        numbersTexture_ = new Texture(context, R.drawable.numbers);
    }

    public void render(float[] mvpMatrix, final ShaderProgram shader, float elapsedTime) {
        if (isVisible_) {
            curScale_ += scaleSpeed_ * elapsedTime;
            for (ScoreNumber number : numbers_) {
                if (Math.abs(curScale_ - endScale_) > 0) {
                    number.setAlpha(curScale_);
                    number.addPosition(new Vector2(0, translateSpeed_ * elapsedTime));
                } else {
                    isVisible_ = false;
                }
                number.draw(mvpMatrix, shader);

            }
        }
    }
}
