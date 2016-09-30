package com.blackteam.dsketches;

// TODO: Нужен ли?
/**
 * Цифра для отрисовки счёта.
 */
public class ScoreNumber extends DisplayableObject {
    public ScoreNumber(Vector2 pos, Texture texture, ShaderProgram shader) {
        super(pos, texture, shader);
    }

    public ScoreNumber(Vector2 pos, Texture texture,
                       float texX, float texY, float texWidth, float texHeight,
                       ShaderProgram shader) {
        super(pos, texture, texX, texY, texWidth, texHeight, shader);
    }

    @Override
    public void dispose() {

    }
}
