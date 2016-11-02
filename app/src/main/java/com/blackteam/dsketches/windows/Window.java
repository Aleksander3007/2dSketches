package com.blackteam.dsketches.windows;

import com.blackteam.dsketches.ContentManager;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

public abstract class Window {
    protected Vector2 pos_;
    protected Size2 size_;

    public abstract void resize(final float windowWidth, final float windowHeight);
    public abstract void render(Graphics graphics);
    public abstract void touchUpHandle(Vector2 worldCoords);

    public boolean hit(Vector2 coords) {
        boolean hitX = (coords.x >= pos_.x) && (coords.x <= pos_.x + size_.width);
        boolean hitY = (coords.y >= pos_.y) && (coords.y <= pos_.y + size_.height);

        return (hitX && hitY);
    }

    public abstract void loadContent(ContentManager contents);

    public Vector2 getPos() {
        return pos_;
    }

    public void setPos(Vector2 pos) {
        this.pos_ = pos;
    }

    public Size2 getSize() {
        return size_;
    }

    public void setSize(Size2 size) {
        this.size_ = size;
    }
}
