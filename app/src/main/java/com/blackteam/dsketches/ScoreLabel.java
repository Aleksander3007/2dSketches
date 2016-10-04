package com.blackteam.dsketches;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class ScoreLabel {
    private Texture numbersTexture_;

    private Vector2 pos_;
    private int score_;
    private ArrayList<ScoreNumber> numbers_ = new ArrayList<ScoreNumber>();
    private float numberHeight_;
    private float numberWidth_;

    public ScoreLabel(final Context context) {
        loadContent(context);
    }

    public void init(final int startScore, final Vector2 pos, final Size2 rectSize) {
        this.pos_ = pos;
        score_ = startScore;
        numberHeight_ = rectSize.height;
        numberWidth_ = rectSize.height;

        addScore(score_);
    }

    public void addScore(int addingScore) {
        setScore(score_ + addingScore);
    }

    public void setScore(int score) {
        score_ = score;

        numbers_.clear();

        if (score_ == 0) {
            Log.i("ScoreLabel.setScore()", "score = 0");
            ScoreNumber scoreNumber = new ScoreNumber(
                    new Vector2(this.pos_.x, this.pos_.y),
                    numbersTexture_, 0, 0, 32, 32);
            scoreNumber.setSize(numberWidth_, numberHeight_);
            numbers_.add(scoreNumber);
        }

        // Выделяем цифры из числа (и записываем в массив).
        int rest = score_;
        while (rest >= 1) {
            int number = rest % 10;
            rest = rest / 10;

            ScoreNumber scoreNumber = new ScoreNumber(
                    new Vector2(this.pos_.x, this.pos_.y), // Правильная позиция устанавливается далее.
                    numbersTexture_, number * 32, 0, 32, 32);
            scoreNumber.setSize(numberWidth_, numberHeight_);
            numbers_.add(scoreNumber);
        }

        for (int iNumber = 0; iNumber < numbers_.size(); iNumber++) {
            numbers_.get(numbers_.size() - iNumber - 1).setPosition(
                    this.pos_.x + iNumber * numberWidth_,
                    this.pos_.y
            );
        }
    }

    public void loadContent(Context context) {
        numbersTexture_ = new Texture(context, R.drawable.numbers);
    }

    public void draw(float[] mvpMatrix, final ShaderProgram shader) {
        for (ScoreNumber number : numbers_) {
            number.draw(mvpMatrix, shader);
        }
    }
}
