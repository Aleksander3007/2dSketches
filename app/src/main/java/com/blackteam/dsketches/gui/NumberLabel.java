package com.blackteam.dsketches.gui;


import android.content.Context;

import com.blackteam.dsketches.R;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс для отображения чисел средствами OpenGL.
 */
public class NumberLabel {
    /** Ширина текстуры. */
    public static final int TEX_WIDTH = 64;
    /** Высота текстуры. */
    public static final int TEX_HEIGHT = 128;

    protected Texture mDigitsTexture;
    protected Vector2 mPos = new Vector2(0, 0);
    protected float mDigitHeight;
    protected float mDigitWidth;

    /** Величина которая отображается. */
    protected int mValue;
    protected CopyOnWriteArrayList<DisplayableObject> mDigits = new CopyOnWriteArrayList<>();

    public NumberLabel(final Texture texture) {
        mDigitsTexture = texture;
    }

    public NumberLabel(final Context context) {
        loadContent(context);
    }

    public void init(final Vector2 pos, final Size2 rectSize) {
        mDigitWidth = rectSize.height;
        mDigitHeight = rectSize.height;
        mPos = new Vector2(0f, 0f);

        for (DisplayableObject digit : mDigits) {
            digit.setSize(mDigitWidth, mDigitHeight);
        }

        setPosition(pos);
    }

    public void setValue(int val) {
        mValue = val;

        mDigits.clear();

        if (mValue == 0) {
            DisplayableObject digit = new DisplayableObject(
                    new Vector2(0f, 0f),
                    mDigitsTexture, 0, 0, TEX_WIDTH, TEX_HEIGHT);
            digit.setSize(mDigitWidth, mDigitHeight);
            mDigits.add(digit);
        }

        // Выделяем цифры из числа (и записываем в массив).
        int rest = mValue;
        while (rest >= 1) {
            int number = rest % 10;
            rest = rest / 10;

            DisplayableObject digit = new DisplayableObject(
                    new Vector2(0f, 0f), // Правильная позиция устанавливается не здесь.
                    mDigitsTexture, number * TEX_WIDTH, 0, TEX_WIDTH, TEX_HEIGHT);
            digit.setSize(mDigitWidth, mDigitHeight);
            mDigits.add(digit);
        }

        setPosition(mPos);
    }

    public void setPosition(final Vector2 pos) {
        mPos = pos;

        // Устанавливаем позицию.
        for (int iDigit = 0; iDigit < mDigits.size(); iDigit++) {
            mDigits.get(mDigits.size() - iDigit - 1).setPosition(
                    mPos.x + iDigit * mDigitWidth,
                    mPos.y
            );
        }
    }

    public void loadContent(Context context) {
        mDigitsTexture = new Texture(context, R.drawable.numbers);
    }

    public void render(Graphics graphics) {
        for (DisplayableObject number : mDigits) {
            number.draw(graphics);
        }
    }
}
