package com.blackteam.dsketches.gui;

// TODO: Нужен ли?

import com.blackteam.dsketches.utils.Vector2;

/**
 * Цифра для отрисовки счёта.
 */
public class ScoreNumber extends DisplayableObject {
    public ScoreNumber(Vector2 pos, Texture texture) {
        super(pos, texture);
    }

    public ScoreNumber(Vector2 pos, Texture texture,
                       float texX, float texY, float texWidth, float texHeight) {
        super(pos, texture, texX, texY, texWidth, texHeight);
    }
}
