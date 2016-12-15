package com.blackteam.dsketches.gui;

import com.blackteam.dsketches.animation.AnimationController;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

// TODO: Может extends Sprite.
// TODO: Вместо Texture использовать TextureRegion.
public class DisplayableObject {
    protected Sprite mSprite;
    /** Координаты левого нижнего угла. */
    protected Vector2 mPos = new Vector2(0, 0);
    /** По умолчанию - без масштабирования. */
    protected Vector2 mScale = new Vector2(1, 1);
    /** Вращение вокруг центра объекта. По умолчанию - без вращения. */
    protected float mRotationDeg = 0;
    protected float mWidth = 1.0f;
    protected float mHeight = 1.0f;
    protected AnimationController mAnimationController;

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
     * @param textureRegion Регион текстуры.
     */
    public DisplayableObject (TextureRegion textureRegion) {
        this(new Vector2(0.0f, 0.0f), textureRegion);
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
     * @param textureRegion Регион текстуры.
     */
    public DisplayableObject(Vector2 pos, TextureRegion textureRegion) {
        this(pos, 0.0f, textureRegion.getTexture(),
                textureRegion.getPos().x, textureRegion.getPos().y,
                textureRegion.getSize().width, textureRegion.getSize().height);
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
        this.mPos = new Vector2(pos.x, pos.y);
        this.mSprite = new Sprite(texture, texX, texY, texWidth, texHeight);
        this.mRotationDeg = rotationDeg;

        mSprite.setPosition(pos);
        mSprite.setRotate(rotationDeg);
    }

    public boolean hit(Vector2 coords) {
        return ((coords.x >= mPos.x) && (coords.x <= (mPos.x + mWidth))) &&
                ((coords.y >= mPos.y) && (coords.y <= (mPos.y + mHeight)));
    }

    public void draw(Graphics graphics) {
        if (mAnimationController != null)
            mAnimationController.update(this, graphics.getElapsedTime());

        mSprite.draw(graphics.getMVPMatrix(), graphics.getShader());
    }

    public void setTexture(Texture texture) {
        if (mSprite == null)
            mSprite = new Sprite(texture);
        else
            mSprite.setTexture(texture);
    }

    public void setTexture(TextureRegion textureRegion) {
        if (mSprite == null)
            mSprite = new Sprite(textureRegion.getTexture(),
                    textureRegion.getPos().x, textureRegion.getPos().y,
                    textureRegion.getSize().width, textureRegion.getSize().height);
        else
            mSprite.setTexture(textureRegion.getTexture(),
                    textureRegion.getPos().x, textureRegion.getPos().y,
                    textureRegion.getSize().width, textureRegion.getSize().height);
    }

    public void setAnimation(AnimationController animationController) {
        this.mAnimationController = animationController;
    }

    public boolean isAnimationFinished() {
        if (mAnimationController != null)
            return mAnimationController.isFinished();
        else
            return true;
    }

    public float getX() { return mPos.x; }
    public float getY() { return mPos.y; }

    public float getWidth() { return mWidth; }
    public float getHeight() { return mHeight; }

    public void setSize(final Size2 size) {
        setSize(size.width, size.height);
    }

    public void setSize(final float width, final float height) {
        mWidth = width;
        mHeight = height;
        mSprite.setScale(width, height);
    }

    public void setSizeCenter(final Size2 size) {
        setSizeCenter(size.width, size.height);
    }

    public void setSizeCenter(float newWidth, float newHeight) {
        Vector2 newPos = new Vector2(
                mPos.x - (newWidth - mWidth) / 2,
                mPos.y - (newHeight - mHeight) / 2
        );
        setSize(newWidth, newHeight);
        setPosition(newPos);
    }

    public void setPosition(final float x, final float y) {
        setPosition(new Vector2(x, y));
    }

    public void setPosition(final Vector2 pos) {
        mPos = new Vector2(pos);
        mSprite.setPosition(pos);
    }

    public void addPosition(final Vector2 amount) {
        mPos.add(amount);
        mSprite.addPosition(amount);
    }

    public Vector2 getPosition() {
        return mPos;
    }

    public void setAlpha(float alphaFactor) {
        mSprite.setAlpha(alphaFactor);
    }

    public void setRotationDeg(final float angleDeg) {
        mRotationDeg = angleDeg;
        mSprite.setRotate(angleDeg);
    }
}
