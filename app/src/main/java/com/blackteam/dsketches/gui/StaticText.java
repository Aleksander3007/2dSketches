package com.blackteam.dsketches.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

public class StaticText extends DisplayableObject {
    private static final int TEXT_COLOR_ = Color.BLACK;

    public StaticText(String text, Vector2 pos, Size2 size) {
        if (text == null) {
            text = "";
        }

        float textSize = 52;
        Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(TEXT_COLOR_);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.width() + 26;
        int height = bounds.height() + 24;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        bitmap.eraseColor(0);
        // Получаем canvas для рисования на нём текста.
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, bitmap.getHeight(), textPaint);

        Texture texture = new Texture(bitmap);
        sprite_ = new Sprite(texture);
        sprite_.setPosition(pos);
        sprite_.setScale(size.width, size.height);
    }
}
