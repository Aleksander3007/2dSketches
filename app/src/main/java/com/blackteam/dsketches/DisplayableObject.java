package com.blackteam.dsketches;

import android.graphics.Bitmap;

public abstract class DisplayableObject {
    protected Bitmap texture_;
    /**
     * Координаты левого нижнего угла.
     */
    protected Vector2 pos_;
    /**
     * По умолчанию - без масштабирования.
     */
    protected Vector2 scale_ = new Vector2(1, 1);
    /**
     * Вращение вокруг центра объекта. По умолчанию - без вращения.
     */
    protected float rotationDeg_ = 0;

    public DisplayableObject(Vector2 pos, Bitmap texture) {
        this.texture_ = texture;
        this.pos_ = pos;
    }

    public DisplayableObject(Vector2 pos, float rotationDeg, Bitmap texture) {
        this.texture_ = texture;
        this.pos_ = pos;
        this.rotationDeg_ = rotationDeg;
    }

    public Bitmap getTexture() {
        return texture_;
    }

    public abstract void dispose();

    public float getX() {
        return this.pos_.x;
    }

    public float getY() {
        return this.pos_.y;
    }

    public float getScaleX() {
        return this.scale_.x;
    }

    public float getScaleY() {
        return this.scale_.y;
    }

    public float getRotationDeg() {
        return this.rotationDeg_;
    }

    public abstract int getWidth();
    public abstract int getHeight();
}
