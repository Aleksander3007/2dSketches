package com.blackteam.dsketches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Текстура (изображение в памяти видеокарты).
 */
public class Texture {

    private int textureId_;

    private int width_;
    private int height_;

    public Texture(Context context, int resourceId) {
        boolean isLoaded = load(context, resourceId);
        if (!isLoaded) throw new IllegalArgumentException("Error loaded texture.");
    }

    public Texture(Bitmap bitmap) {
        boolean isLoaded = load(bitmap);
        if (!isLoaded) throw new IllegalArgumentException("Error loaded texture.");
    }

    public int getId() {
        return textureId_;
    }

    public int getWidth() { return width_; }

    public int getHeight() { return height_; }

    /**
     * Загрузка текстуры.
     * @param context Контекст.
     * @param resourceId Индентификатор текстуры.
     * @return true - текстура удачно загружена.
     */
    private boolean load(Context context, int resourceId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        return load(bitmap);
    }

    private boolean load(final Bitmap bitmap) {
        final int[] textureIds = new int[1];
        // В массив запишет свободный номер текстуры.
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return false;
        }

        width_ = bitmap.getWidth();
        height_ = bitmap.getHeight();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Привязка текстуры.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        // Переписываем Bitmap в память видеокарты.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        setFilter();
        // Сброс привязки текстуры.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        bitmap.recycle();

        textureId_ = textureIds[0];

        Log.i("Sprite.textureId", String.valueOf(textureId_));

        return true;
    }

    private void setFilter() {
        // На меньшем количестве пикселей экрана отображается больше кол-во пикселей текстуры.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        // На большом количестве пикселей экрана отображается меньшее кол-во пикселей текстуры.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }
}
