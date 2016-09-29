package com.blackteam.dsketches;

import android.graphics.Shader;

public abstract class DisplayableObject {
    protected Sprite sprite_;
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

    protected float width_ = 1.0f;
    protected float height_ = 1.0f;

    public DisplayableObject(Vector2 pos, Texture texture, ShaderProgram shader) {
        this(pos, 0f, texture, shader);
    }

    public DisplayableObject(Vector2 pos, float rotationDeg,
                             Texture texture, ShaderProgram shader) {
        this.pos_ = pos;
        this.sprite_ = new Sprite(texture, shader);
        this.rotationDeg_ = rotationDeg;

        sprite_.setPosition(pos);
        sprite_.setRotate(rotationDeg);
    }

    public void draw(float[] mvpMatrix) {
        sprite_.draw(mvpMatrix);
    }

    public Sprite getSprite() {
        return sprite_;
    }

    public abstract void dispose();

    public float getX() { return pos_.x; }
    public float getY() { return pos_.y; }

    public float getScaleX() { return scale_.x; }
    public float getScaleY() { return scale_.y; }

    public float getRotationDeg() { return rotationDeg_; }

    public float getWidth() { return width_; }
    public float getHeight() { return height_; }

    public void setSize(float width, float height) {
        width_ = width;
        height_ = height;
        sprite_.setScale(width, height);
    }


}
