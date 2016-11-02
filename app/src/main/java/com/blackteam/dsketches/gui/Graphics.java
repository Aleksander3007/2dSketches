package com.blackteam.dsketches.gui;

/**
 * Содержит основную информацию необходимую для отображения.
 */
public class Graphics {
    private float[] mvpMatrix_;
    private ShaderProgram shader_;
    private float elapsedTime_;

    public Graphics(float[] mvpMatrix, final ShaderProgram shader) {
        this.mvpMatrix_ = mvpMatrix;
        this.shader_ = shader;
        this.elapsedTime_ = 0;
    }

    public void setElapsedTime(final float elapsedTime) {
        this.elapsedTime_ = elapsedTime;
    }

    public float getElapsedTime() {
        return elapsedTime_;
    }

    public float[] getMVPMatrix() {
        return mvpMatrix_;
    }

    public ShaderProgram getShader() {
        return shader_;
    }
}
