package com.blackteam.dsketches.gui;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Спрайт.
 */
public class Sprite {
    private static final float SQUARE_COORDS[] = {
            0f, 1f, 0.0f, // top left;
            0f, 0f, 0.0f, // bottom left;
            1f, 1f, 0.0f,  // top right;
            1f, 0f, 0.0f   // bottom right;
    };

    private float mTextureCoords[] = {
            // Mapping coordinates for the vertices:
            0.0f, 0.0f,     // bottom left  (V1)
            0.0f, 1.0f,     // top left     (V2)
            1.0f, 0.0f,     // bottom right (V3)
            1.0f, 1.0f      // top right    (V4)
    };

    private static final int COORDS_PER_VERTEX_ = 3;
    private static final int VERTEX_COUNT_ = SQUARE_COORDS.length / COORDS_PER_VERTEX_;
    private static final int VERTEX_STRIDE_ = COORDS_PER_VERTEX_ * 4; // 4 bytes per vertex.
    private static final int TEX_COORDS_PER_VERTEX_ = 2;
    private static final int TEXTURE_STRIDE_ = TEX_COORDS_PER_VERTEX_ * 4;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    private int mPositionHandle;
    private int mTexturePosHandle;
    private int mMvpMatrixHandle;
    private int mAlphaFactorHandle;

    private Texture mTexture;
    private Vector2 mTexRegionPos;
    private Size2 mTexRegionSize;

    private float[] mTranslateMatrix = new float[16];
    private float[] mScaleMatrix     = new float[16];
    private float[] mRotateMatrix    = new float[16];
    private float[] mModelMatrix     = new float[16];
    private float[] mMatrix          = new float[16];

    private float mAlphaFactor;

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
        setTexture(texture, x, y, width, height);
        prepareData();
        resetMatrices();
    }

    /**
     * Установка текстуры.
     * @param texture Текстура.
     */
    public void setTexture(Texture texture) {
        setTexture(texture, 0f, 0f, texture.getWidth(), texture.getHeight());
    }

    /**
     * Установка текстуры.
     * @param texture Текстура.
     * @param x X-позиция региона в пикселях из указанной текстуры.
     * @param y Y-позиция региона в пикселях из указанной текстуры.
     * @param width Ширина региона в пикселях из указанной текстуры.
     * @param height Высота региона в пикселях из указанной текстуры.
     */
    public void setTexture(Texture texture, float x, float y, float width, float height) {
        mTexture = texture;
        this.mTexRegionPos = new Vector2(x, y);
        this.mTexRegionSize = new Size2(width, height);
    }

    /**
     * Отрисовка спрайта.
     * <br/><b>Note: На shader можно ссылаться только в потоке Open GL ES.</b>
     * @param mvpMatrix Матрица мира.
     * @param shader Шейдер.
     */
    //
    public void draw(float[] mvpMatrix, final ShaderProgram shader) {

        prepareTextureCoords();
        getHandlers(shader);
        bindData();

        Matrix.multiplyMM(mMatrix, 0, mvpMatrix, 0, mModelMatrix, 0);
        // Pass the projection and view transformation to the shader.
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMatrix, 0);
        GLES20.glUniform1f(mAlphaFactorHandle, mAlphaFactor);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.getId());
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT_);
    }

    /**
     * Установка множителя альфа-канала.
     * @param alpha Множитель.
     */
    public void setAlpha(float alpha) {
        mAlphaFactor = alpha;
    }

    public void setPosition(final Vector2 pos) {
        Matrix.setIdentityM(mTranslateMatrix, 0);
        addPosition(pos);
    }

    public void addPosition(final Vector2 amount) {
        Matrix.translateM(mTranslateMatrix, 0, amount.x, amount.y, 0.0f);
        buildModelMatrix();
    }

    public void setRotate(final float angleDeg) {
        Matrix.setIdentityM(mRotateMatrix, 0);
        Matrix.rotateM(mRotateMatrix, 0, angleDeg, 0.0f, 0.0f, 1.0f);
        buildModelMatrix();
    }

    public void setScale(final float scaleX, final float scaleY) {
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.scaleM(mScaleMatrix, 0, scaleX, scaleY, 0.0f);
        buildModelMatrix();
    }

    public void setScale(final float scaleVal) {
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.scaleM(mScaleMatrix, 0, scaleVal, scaleVal, 0.0f);
        buildModelMatrix();
    }

    private void resetMatrices() {
        Matrix.setIdentityM(mTranslateMatrix, 0);
        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.setIdentityM(mRotateMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    private void buildModelMatrix() {
        Matrix.multiplyMM(mModelMatrix, 0, mTranslateMatrix, 0, mRotateMatrix, 0);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mScaleMatrix, 0);
    }

    /**
     * Подготовка данных массива вершин и т.п.
     */
    private void prepareData() {
        // Initialize vertex byte buffer for shape coordinates.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float).
                SQUARE_COORDS.length * 4);
        // Use the device hardware's native byte order.
        byteBuffer.order(ByteOrder.nativeOrder());
        // Create a floating point buffer from the ByteBuffer.
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(SQUARE_COORDS);
        mVertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(mTextureCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuffer.asFloatBuffer();
        mTextureBuffer.put(mTextureCoords);
        mTextureBuffer.position(0);

        mAlphaFactor = 1.0f;
    }

    /**
     * Привязка данных (координат и т.п.) к шейдеру.
     */
    private void bindData() {
        // Привязка координат спрайта.
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX_,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE_, mVertexBuffer);
        // Привязка текстурных координат.
        GLES20.glVertexAttribPointer(mTexturePosHandle, TEX_COORDS_PER_VERTEX_, GLES20.GL_FLOAT,
                false, TEXTURE_STRIDE_, mTextureBuffer);
    }

    /**
     * Получение Handler-ов всех атрибутов шейдер.
     * @param shader Шейдер.
     */
    private void getHandlers(ShaderProgram shader) {
        mPositionHandle = shader.getAttribLocation(ShaderProgram.POSITION_ATTR);
        mTexturePosHandle = shader.getAttribLocation(ShaderProgram.TEXCOORD_ATTR);
        mMvpMatrixHandle = shader.getUniformLocation(ShaderProgram.MATRIX_ATTR);
        mAlphaFactorHandle = shader.getUniformLocation(ShaderProgram.ALPHA_FACTOR_ATTR);
    }

    /**
     * Подготавливаем массив с координатами текстуры.
     */
    private void prepareTextureCoords() {
        // Units per pixel.
        float uppX = 1.0f / mTexture.getWidth();
        float uppY = 1.0f / mTexture.getHeight();

        float xUnits = this.mTexRegionPos.x * uppX;
        float yUnits = this.mTexRegionPos.y * uppY;

        float widthUnits = 1.0f;
        float heightUnits = 1.0f;
        if ((this.mTexRegionSize.width > 0) &&(this.mTexRegionSize.height > 0)) {
            widthUnits = this.mTexRegionSize.width * uppX;
            heightUnits = this.mTexRegionSize.height * uppY;
        }
        else {
            this.mTexRegionSize.width = mTexture.getWidth();
            this.mTexRegionSize.height = mTexture.getHeight();
        }

        // Bottom left (V1).
        mTextureCoords[0] = xUnits;
        mTextureCoords[1] = yUnits;
        // Top left (V2).
        mTextureCoords[2] = xUnits;
        mTextureCoords[3] = yUnits + heightUnits;
        // Bottom right (V3).
        mTextureCoords[4] = xUnits + widthUnits;
        mTextureCoords[5] = yUnits;
        // Top right (V4).
        mTextureCoords[6] = xUnits + widthUnits;
        mTextureCoords[7] = yUnits + heightUnits;


        for (int iCoord = 0; iCoord < mTextureCoords.length; iCoord++) {
            mTextureBuffer.put(iCoord, mTextureCoords[iCoord]);
        }
        mTextureBuffer.position(0);
    }

}