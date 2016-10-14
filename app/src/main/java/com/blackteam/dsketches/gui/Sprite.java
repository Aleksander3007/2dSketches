package com.blackteam.dsketches.gui;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.blackteam.dsketches.utils.Vector2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Спрайт.
 */
public class Sprite {
    private static final float squareCoords[] = {
            0f, 1f, 0.0f, // top left;
            0f, 0f, 0.0f, // bottom left;
            1f, 1f, 0.0f,  // top right;
            1f, 0f, 0.0f   // bottom right;
    };

    private float textureCoords[] = {
            // Mapping coordinates for the vertices:
            0.0f, 0.0f,     // bottom left  (V1)
            0.0f, 1.0f,     // top left     (V2)
            1.0f, 0.0f,     // bottom right (V3)
            1.0f, 1.0f      // top right    (V4)
    };

    private static final int COORDS_PER_VERTEX_ = 3;
    private static final int VERTEX_COUNT_ = squareCoords.length / COORDS_PER_VERTEX_;
    private static final int VERTEX_STRIDE_ = COORDS_PER_VERTEX_ * 4; // 4 bytes per vertex.
    private static final int TEX_COORDS_PER_VERTEX_ = 2;
    private static final int TEXTURE_STRIDE_ = TEX_COORDS_PER_VERTEX_ * 4;

    private FloatBuffer vertexBuffer_;
    private FloatBuffer textureBuffer_;

    private int positionHandle_;
    private int texturePosHandle_;
    private int mvpMatrixHandle_;
    private int alphaFactorHandle_;

    private Texture texture_;

    private float[] translateMatrix_ = new float[16];
    private float[] scaleMatrix_ = new float[16];
    private float[] rotateMatrix_ = new float[16];
    private float[] modelMatrix_ = new float[16];
    private float[] matrix_ = new float[16];

    private float alphaFactor_;

    public Sprite(Texture texture) {
        this(texture, 0.0f, 0.0f, texture.getWidth(), texture.getHeight());
    }

    /**
     * Конструктор.
     * @param texture Текстура.
     * @param x X-позиция региона в пикселях из указанной текстуры.
     * @param y Y-позиция региона в пикселях из указанной текстуры.
     * @param width Ширина региона в пикселях из указанной текстуры.
     * @param height Высота региона в пикселях из указанной текстуры.
     */
    public Sprite(Texture texture, float x, float y, float width, float height) {
        // Units per pixel.
        float uppX = 1.0f / texture.getWidth();
        float uppY = 1.0f / texture.getHeight();

        float xUnits = x * uppX;
        float yUnits = y * uppY;
        float widthUnits = width * uppX;
        float heightUnits = height * uppY;

        // Bottom left (V1).
        textureCoords[0] = xUnits;
        textureCoords[1] = yUnits;
        // Top left (V2).
        textureCoords[2] = xUnits;
        textureCoords[3] = yUnits + heightUnits;
        // Bottom right (V3).
        textureCoords[4] = xUnits + widthUnits;
        textureCoords[5] = yUnits;
        // Top right (V4).
        textureCoords[6] = xUnits + widthUnits;
        textureCoords[7] = yUnits + heightUnits;

        this.texture_ = texture;
        prepareData();
        resetMatrices();
    }

    /**
     * Отрисовка спрайта.
     * <br/><b>Note: На shader можно ссылаться только в потоке Open GL ES.</b>
     * @param mvpMatrix Матрица мира.
     * @param shader Шейдер.
     */
    //
    public void draw(float[] mvpMatrix, final ShaderProgram shader) {

        getHandlers(shader);
        bindData();

        Matrix.multiplyMM(matrix_, 0, mvpMatrix, 0, modelMatrix_, 0);
        // Pass the projection and view transformation to the shader.
        GLES20.glUniformMatrix4fv(mvpMatrixHandle_, 1, false, matrix_, 0);
        GLES20.glUniform1f(alphaFactorHandle_, alphaFactor_);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_.getId());
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT_);
    }

    /**
     * Установка множителя альфа-канала.
     * @param alpha Множитель.
     */
    public void setAlpha(float alpha) {
        alphaFactor_ = alpha;
    }

    public void setPosition(Vector2 pos) {
        Matrix.setIdentityM(translateMatrix_, 0);
        addPosition(pos);
    }

    public void addPosition(Vector2 pos) {
        Matrix.translateM(translateMatrix_, 0, pos.x, pos.y, 0.0f);
        buildModelMatrix();
    }

    public void setRotate(float angleDeg) {
        Matrix.setIdentityM(rotateMatrix_, 0);
        Matrix.rotateM(rotateMatrix_, 0, angleDeg, 0.0f, 0.0f, 1.0f);
        buildModelMatrix();
    }

    public void setScale(float scaleX, float scaleY) {
        Matrix.setIdentityM(scaleMatrix_, 0);
        Matrix.scaleM(scaleMatrix_, 0, scaleX, scaleY, 0.0f);
        buildModelMatrix();
    }

    public void setScale(float scaleVal) {
        Matrix.setIdentityM(scaleMatrix_, 0);
        Matrix.scaleM(scaleMatrix_, 0, scaleVal, scaleVal, 0.0f);
        buildModelMatrix();
    }

    private void resetMatrices() {
        Matrix.setIdentityM(translateMatrix_, 0);
        Matrix.setIdentityM(scaleMatrix_, 0);
        Matrix.setIdentityM(rotateMatrix_, 0);
        Matrix.setIdentityM(modelMatrix_, 0);
    }

    private void buildModelMatrix() {
        Matrix.multiplyMM(modelMatrix_, 0, translateMatrix_, 0, rotateMatrix_, 0);
        Matrix.multiplyMM(modelMatrix_, 0, modelMatrix_, 0, scaleMatrix_, 0);
    }

    /**
     * Подготовка данных массива вершин и т.п.
     */
    private void prepareData() {
        // Initialize vertex byte buffer for shape coordinates.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float).
                squareCoords.length * 4);
        // Use the device hardware's native byte order.
        byteBuffer.order(ByteOrder.nativeOrder());
        // Create a floating point buffer from the ByteBuffer.
        vertexBuffer_ = byteBuffer.asFloatBuffer();
        vertexBuffer_.put(squareCoords);
        vertexBuffer_.position(0);

        byteBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer_ = byteBuffer.asFloatBuffer();
        textureBuffer_.put(textureCoords);
        textureBuffer_.position(0);

        alphaFactor_ = 1.0f;
    }

    /**
     * Привязка данных (координат и т.п.) к шейдеру.
     */
    private void bindData() {
        // Привязка координат спрайта.
        GLES20.glVertexAttribPointer(positionHandle_, COORDS_PER_VERTEX_,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE_, vertexBuffer_);
        // Привязка текстурных координат.
        GLES20.glVertexAttribPointer(texturePosHandle_, TEX_COORDS_PER_VERTEX_, GLES20.GL_FLOAT,
                false, TEXTURE_STRIDE_, textureBuffer_);
    }

    /**
     * Получение Handler-ов всех атрибутов шейдер.
     * @param shader Шейдер.
     */
    private void getHandlers(ShaderProgram shader) {
        positionHandle_ = shader.getAttribLocation(ShaderProgram.POSITION_ATTR);
        texturePosHandle_ = shader.getAttribLocation(ShaderProgram.TEXCOORD_ATTR);
        mvpMatrixHandle_ = shader.getUniformLocation(ShaderProgram.MATRIX_ATTR);
        alphaFactorHandle_ = shader.getUniformLocation(ShaderProgram.ALPHA_FACTOR_ATTR);
    }
}