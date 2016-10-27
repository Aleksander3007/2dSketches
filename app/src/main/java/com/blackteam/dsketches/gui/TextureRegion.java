package com.blackteam.dsketches.gui;

import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Регион на текстуре.
 */
public class TextureRegion {
    private Texture texture_;
    private Vector2 pos_;
    private Size2 size_;

    public TextureRegion(Texture texture, Vector2 pos, Size2 size) {
        this.texture_ = texture;
        this.pos_ = pos;
        this.size_ = size;
    }

    public Texture getTexture() {
        return texture_;
    }

    public Vector2 getPos() {
        return pos_;
    }

    public Size2 getSize() {
        return size_;
    }
}
