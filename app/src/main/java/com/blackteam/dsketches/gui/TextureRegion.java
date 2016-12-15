package com.blackteam.dsketches.gui;

import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Регион на текстуре.
 */
public class TextureRegion {
    private Texture mTexture;
    private Vector2 mPos;
    private Size2 mSize;

    public TextureRegion(Texture texture, Vector2 pos, Size2 size) {
        this.mTexture = texture;
        this.mPos = pos;
        this.mSize = size;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public Vector2 getPos() {
        return mPos;
    }

    public Size2 getSize() {
        return mSize;
    }
}
