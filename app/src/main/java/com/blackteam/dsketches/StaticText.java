package com.blackteam.dsketches;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.blackteam.dsketches.Utils.Size2;
import com.blackteam.dsketches.Utils.Vector2;

public class StaticText extends DisplayableObject {
    private static final int TEXT_COLOR_ = Color.BLACK;

    public StaticText(String text, Vector2 pos, Size2 size) {
        if (text == null) {
            text = "";
        }

        Bitmap bitmap = Bitmap.createBitmap(256, 128, Bitmap.Config.ARGB_4444);
        // Получаем canvas для рисования на нём текста.
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);

        float textSize = 2.0f * bitmap.getWidth() / text.length();
        Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(TEXT_COLOR_);
        canvas.drawText(text, 0, bitmap.getHeight(), textPaint);

        Texture texture = new Texture(bitmap);
        sprite_ = new Sprite(texture);
        sprite_.setPosition(pos);
        sprite_.setScale(size.width, size.height);
    }

    @Override
    public void dispose() {

    }
}
