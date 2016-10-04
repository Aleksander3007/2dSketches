package com.blackteam.dsketches;

public class RestartButton extends DisplayableObject {

    public RestartButton(Texture texture) {
        super(texture);
    }

    @Override
    public void dispose() {

    }

    public void init(Vector2 pos, Size2 size) {
        setSize(size.width, size.height);
        setPosition(pos);
    }
}
