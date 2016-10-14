package com.blackteam.dsketches.gui;

import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

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
