package com.blackteam.dsketches.gui;

/**
 * Содержит основную информацию необходимую для отображения.
 */
public class Graphics {
    private float[] mMvpMatrix;
    private ShaderProgram mShader;
    private float mElapsedTime;

    public Graphics(float[] mvpMatrix, final ShaderProgram shader) {
        this.mMvpMatrix = mvpMatrix;
        this.mShader = shader;
        this.mElapsedTime = 0;
    }

    public void setElapsedTime(final float elapsedTime) {
        this.mElapsedTime = elapsedTime;
    }

    public float getElapsedTime() {
        return mElapsedTime;
    }

    public float[] getMVPMatrix() {
        return mMvpMatrix;
    }

    public ShaderProgram getShader() {
        return mShader;
    }
}
