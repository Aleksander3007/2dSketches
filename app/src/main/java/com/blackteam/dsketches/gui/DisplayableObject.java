package com.blackteam.dsketches.gui;

import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

// TODO: Может extends Sprite.
// TODO: Вместо Texture использовать TextureRegion.
public class DisplayableObject {
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
     */
    protected DisplayableObject() {}

    /**
     * Конструктор.
     * @param texture Текстура.
     */
    public DisplayableObject (Texture texture) {
        this(new Vector2(0.0f, 0.0f), 0.0f, texture);
    }

    /**
     * Конструктор.
     * @param pos Позиция объекта.
     * @param texture Текстура.
     */
    public DisplayableObject(Vector2 pos, Texture texture) {
        this(pos, 0.0f, texture);
    }

    /**
     * Конструктор.
     * @param pos Позиция объекта.
     * @param rotationDeg Угол поворота объекта в градусах.
     * @param texture Текстура.
     */
    public DisplayableObject(Vector2 pos, float rotationDeg, Texture texture) {
        this(pos, rotationDeg, texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    /**
     * Конструктор.
     * @param pos Позиция объекта.
     * @param texture Текстура.
     * @param texX X-позиция региона в пикселях из указанной текстуры.
     * @param texY Y-позиция региона в пикселях из указанной текстуры.
     * @param texWidth Ширина региона в пикселях из указанной текстуры.
     * @param texHeight Высота региона в пикселях из указанной текстуры.
     */
    public DisplayableObject(Vector2 pos, Texture texture,
                             float texX, float texY, float texWidth, float texHeight) {
        this(pos, 0.0f, texture, texX, texY, texWidth, texHeight);
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
     */
    public DisplayableObject(Vector2 pos, float rotationDeg, Texture texture,
                             float texX, float texY, float texWidth, float texHeight) {
        if (texture == null) {
            throw new IllegalArgumentException("Texture is not loaded.");
        }
        this.pos_ = new Vector2(pos.x, pos.y);
        this.sprite_ = new Sprite(texture, texX, texY, texWidth, texHeight);
        this.rotationDeg_ = rotationDeg;

        sprite_.setPosition(pos);
        sprite_.setRotate(rotationDeg);
    }

    public boolean hit(Vector2 coords) {
        return ((coords.x >= pos_.x) && (coords.x <= (pos_.x + width_))) &&
                ((coords.y >= pos_.y) && (coords.y <= (pos_.y + height_)));
    }

    public void draw(float[] mvpMatrix, final ShaderProgram shader) {
        sprite_.draw(mvpMatrix, shader);
    }

    public void setTexture(Texture texture) {
        if (sprite_ == null)
            sprite_ = new Sprite(texture);
        else
            sprite_.setTexture(texture);
    }

    public float getX() { return pos_.x; }
    public float getY() { return pos_.y; }

    public float getWidth() { return width_; }
    public float getHeight() { return height_; }

    public void setSize(Size2 size) {
        setSize(size.width, size.height);
    }

    public void setSize(float width, float height) {
        width_ = width;
        height_ = height;
        sprite_.setScale(width, height);
    }

    public void setSizeCenter(float newWidth, float newHeight) {
        Vector2 newPos = new Vector2(
                pos_.x - (newWidth - width_) / 2,
                pos_.y - (newHeight - height_) / 2
        );
        setSize(newWidth, newHeight);
        setPosition(newPos);
    }

    public void setPosition(final float x, final float y) {
        setPosition(new Vector2(x, y));
    }

    public void setPosition(final Vector2 pos) {
        pos_ = new Vector2(pos.x, pos.y);
        sprite_.setPosition(pos);
    }

    public void addPosition(final Vector2 amount) {
        // TODO: Переопределить операцию "+".
        pos_.x += amount.x;
        pos_.y += amount.y;
        sprite_.addPosition(amount);
    }

    public Vector2 getPosition() {
        return  pos_;
    }

    public void setAlpha(float alphaFactor) {
        sprite_.setAlpha(alphaFactor);
    }

    public void setRotationDeg(final float angleDeg) {
        rotationDeg_ = angleDeg;
        sprite_.setRotate(angleDeg);
    }
}
