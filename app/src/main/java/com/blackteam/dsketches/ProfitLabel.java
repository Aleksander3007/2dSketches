package com.blackteam.dsketches;

import android.content.Context;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Количество очков полученное с текущего действия.
 */
public class ProfitLabel {
    private static final float START_ALPHA_ = 1.0f;
    private static final float END_ALPHA_ = 0.85f;
    private static final float ALPHA_SPEED_ = -0.0014f; // units per ms.
    private static final float TRANSLATE_SPEED_ = 0.0006f; // units per ms.

    private Texture numbersTexture_;

    private Vector2 pos_;
    private int value_;
    private CopyOnWriteArrayList<ScoreNumber> numbers_ = new CopyOnWriteArrayList<>();
    private float numberHeight_;
    private float numberWidth_;

    private float curAlpha_;

    private boolean isVisible_;

    public ProfitLabel(final Context context) {
        loadContent(context);
    }

    public void init(final Size2 rectSize) {
        numberHeight_ = rectSize.height;
        numberWidth_ = rectSize.height;
    }

    public void setScore(int val, Vector2 pos) {
        value_ = val;
        pos_ = pos;

        numbers_.clear();

        if (value_ == 0) {
            // TODO: Текстуру должен знать ScoreNumber.
            // TODO: пока используем ScoreNumber.
            ScoreNumber scoreNumber = new ScoreNumber(
                    new Vector2(pos_.x, pos_.y),
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
                    new Vector2(pos_.x, pos_.y), // Правильная позиция устанавливается далее.
                    numbersTexture_, number * 32, 0, 32, 32);
            scoreNumber.setSize(numberWidth_, numberHeight_);
            numbers_.add(scoreNumber);
        }

        // Устанавливаем позицию.
        for (int iNumber = 0; iNumber < numbers_.size(); iNumber++) {
            numbers_.get(numbers_.size() - iNumber - 1).setPosition(
                    pos_.x + iNumber * numberWidth_,
                    pos_.y - 2 * numberWidth_
            );
        }

        curAlpha_ = START_ALPHA_;
        isVisible_ = true;
    }

    // TODO: В ScoreLabel используется та же текстуры, дублирование!!
    // По идеи должен быть AssetsManager, как в libgdx.
    public void loadContent(Context context) {
        numbersTexture_ = new Texture(context, R.drawable.profit_numbers);
    }

    public void render(float[] mvpMatrix, final ShaderProgram shader, float elapsedTime) {
        if (isVisible_) {
            curAlpha_ += ALPHA_SPEED_ * elapsedTime;
            for (ScoreNumber number : numbers_) {
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
