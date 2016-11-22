package com.blackteam.dsketches.gui;


import android.content.Context;

import com.blackteam.dsketches.R;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.concurrent.CopyOnWriteArrayList;

public class NumberLabel {
    /** Ширина текстуры. */
    public static final int TEX_WIDTH = 64;
    /** Высота текстуры. */
    public static final int TEX_HEIGHT = 128;

    protected Texture digitsTexture_;
    protected Vector2 pos_ = new Vector2(0, 0);
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

    public void init(final Vector2 pos, final Size2 rectSize) {
        digitWidth_ = rectSize.height;
        digitHeight_ = rectSize.height;
        pos_ = new Vector2(0f, 0f);

        for (DisplayableObject digit : digits_) {
            digit.setSize(digitWidth_, digitHeight_);
        }

        setPosition(pos);
    }

    public void setValue(int val) {
        value_ = val;

        digits_.clear();

        if (value_ == 0) {
            DisplayableObject digit = new DisplayableObject(
                    new Vector2(0f, 0f),
                    digitsTexture_, 0, 0, TEX_WIDTH, TEX_HEIGHT);
            digit.setSize(digitWidth_, digitHeight_);
            digits_.add(digit);
        }

        // Выделяем цифры из числа (и записываем в массив).
        int rest = value_;
        while (rest >= 1) {
            int number = rest % 10;
            rest = rest / 10;

            DisplayableObject digit = new DisplayableObject(
                    new Vector2(0f, 0f), // Правильная позиция устанавливается не здесь.
                    digitsTexture_, number * TEX_WIDTH, 0, TEX_WIDTH, TEX_HEIGHT);
            digit.setSize(digitWidth_, digitHeight_);
            digits_.add(digit);
        }

        setPosition(pos_);
    }

    public void setPosition(final Vector2 pos) {
        pos_ = pos;

        // Устанавливаем позицию.
        for (int iDigit = 0; iDigit < digits_.size(); iDigit++) {
            digits_.get(digits_.size() - iDigit - 1).setPosition(
                    pos_.x + iDigit * digitWidth_,
                    pos_.y
            );
        }
    }

    public void loadContent(Context context) {
        digitsTexture_ = new Texture(context, R.drawable.profit_numbers);
    }

    public void render(Graphics graphics) {
        for (DisplayableObject number : digits_) {
            number.draw(graphics);
        }
    }
}