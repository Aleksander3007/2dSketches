package com.blackteam.dsketches;

public class RestartButton extends DisplayableObject {

    public RestartButton(Texture texture) {
        super(texture);
    }

    @Override
    public void dispose() {

    }

    public boolean hit(Vector2 coords) {
        return ((coords.x >= pos_.x) && (coords.x <= (pos_.x + width_))) &&
                ((coords.y >= pos_.y) && (coords.y <= (pos_.y + height_)));
    }

    public void init(Vector2 pos, Size2 size) {
        setSize(size.width, size.height);
        setPosition(pos);
    }
}
