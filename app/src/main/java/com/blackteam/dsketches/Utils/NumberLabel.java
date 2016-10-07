package com.blackteam.dsketches.Utils;


import android.content.Context;
import android.util.Log;

import com.blackteam.dsketches.DisplayableObject;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.ScoreNumber;
import com.blackteam.dsketches.ShaderProgram;
import com.blackteam.dsketches.Texture;

import java.util.concurrent.CopyOnWriteArrayList;

public class NumberLabel {
    protected Texture digitsTexture_;
    protected Vector2 pos_;
    protected float digitHeight_;
    protected float digitWidth_;

    protected int value_;
    protected CopyOnWriteArrayList<DisplayableObject> digits_ = new CopyOnWriteArrayList<>();

    public NumberLabel(final Texture texture) {
        digitsTexture_ = texture;
    }

    public NumberLabel(final Context context) {
        loadContent(context);
    }

    public void init(final Size2 rectSize) {
        digitHeight_ = rectSize.height;
        digitWidth_ = rectSize.height;
        pos_ = new Vector2(0f, 0f);
    }

    public void setValue(int val) {
        value_ = val;

        digits_.clear();

        if (value_ == 0) {
            DisplayableObject digit = new DisplayableObject(
                    new Vector2(0f, 0f),
                    digitsTexture_, 0, 0, 32, 32);
            digit.setSize(digitWidth_, digitHeight_);
            digits_.add(digit);
        }

        // Выделяем цифры из числа (и записываем в массив).
        int rest = value_;
        while (rest >= 1) {
            int number = rest % 10;
            rest = rest / 10;

            DisplayableObject digit = new ScoreNumber(
                    new Vector2(0f, 0f), // Правильная позиция устанавливается не здесь.
                    digitsTexture_, number * 32, 0, 32, 32);
            digit.setSize(digitWidth_, digitHeight_);
            digits_.add(digit);
        }

        setPosition(pos_);
    }

    public void setPosition(Vector2 pos) {
        pos_ = pos;

        // Устанавливаем позицию.
        for (int iDigit = 0; iDigit < digits_.size(); iDigit++) {
            digits_.get(digits_.size() - iDigit - 1).setPosition(
                    pos_.x + iDigit * digitWidth_,
                    pos_.y
            );
        }
    }

    // TODO: ProfitLabel наследует от сюда, но использует другую текстуры!
    // TODO: По идеи должен быть AssetsManager, как в libgdx.
    public void loadContent(Context context) {
        digitsTexture_ = new Texture(context, R.drawable.profit_numbers);
    }

    public void render(float[] mvpMatrix, final ShaderProgram shader) {
        for (DisplayableObject number : digits_) {
            number.draw(mvpMatrix, shader);
        }
    }
}