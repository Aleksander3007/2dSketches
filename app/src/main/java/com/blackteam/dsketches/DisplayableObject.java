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

    /**
     * Конструктор.
     * @param pos Позиция объекта.
     * @param texture Текстура.
     * @param shader Шейдер.
     */
    public DisplayableObject(Vector2 pos, Texture texture, ShaderProgram shader) {
        this(pos, 0f, texture, shader);
    }

    /**
     * Конструктор.
     * @param pos Позиция объекта.
     * @param rotationDeg Угол поворота объекта в градусах.
     * @param texture Текстура.
     * @param shader Шейдер.
     */
    public DisplayableObject(Vector2 pos, float rotationDeg,
                             Texture texture, ShaderProgram shader) {
        this(pos, rotationDeg, texture, 0, 0, texture.getWidth(), texture.getHeight(), shader);
    }

    /**
     * Конструктор.
     * @param pos Позиция объекта.
     * @param texture Текстура.
     * @param texX X-позиция региона в пикселях из указанной текстуры.
     * @param texY Y-позиция региона в пикселях из указанной текстуры.
     * @param texWidth Ширина региона в пикселях из указанной текстуры.
     * @param texHeight Высота региона в пикселях из указанной текстуры.
     * @param shader Шейдер.
     */
    public DisplayableObject(Vector2 pos, Texture texture,
                             float texX, float texY, float texWidth, float texHeight,
                             ShaderProgram shader) {
        this(pos, 0.0f, texture, texX, texY, texWidth, texHeight, shader);
    }

    /**
     * Конструктор.
     * @param pos Позиция объекта.
     * @param rotationDeg Угол поворота объекта в градусах.
     * @param texture Текстура.
     * @param texX X-позиция региона в пикселях из указанной текстуры.
     * @param texY Y-позиция региона в пикселях из указанной текстуры.
     * @param texWidth Ширина региона в пикселях из указанной текстуры.
     * @param texHeight Высота региона в пикселях из указанной текстуры.
     * @param shader Шейдер.
     */
    public DisplayableObject(Vector2 pos, float rotationDeg, Texture texture,
                             float texX, float texY, float texWidth, float texHeight,
                             ShaderProgram shader) {
        this.pos_ = pos;
        this.sprite_ = new Sprite(texture, texX, texY, texWidth, texHeight, shader);
        this.rotationDeg_ = rotationDeg;

        sprite_.setPosition(pos);
        sprite_.setRotate(rotationDeg);
    }

    public void draw(float[] mvpMatrix) {
        sprite_.draw(mvpMatrix);
    }

    public abstract void dispose();

    public float getX() { return pos_.x; }
    public float getY() { return pos_.y; }

    public float getWidth() { return width_; }
    public float getHeight() { return height_; }

    public void setSize(float width, float height) {
        width_ = width;
        height_ = height;
        sprite_.setScale(width, height);
    }

    public void setPosition(final float x, final float y) {
        setPosition(new Vector2(x, y));
    }

    public void setPosition(Vector2 pos) {
        sprite_.setPosition(pos);
    }
}
