package com.blackteam.dsketches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Текстура.
 */
public class Sprite {
    private static final float squareCoords[] = {
            -0.5f,  0.5f, 0.0f, // top left;
            -0.5f, -0.5f, 0.0f, // bottom left;
            0.5f,  0.5f, 0.0f,  // top right;
            0.5f, -0.5f, 0.0f   // bottom right;
    };

    private static final float textureCoords[] = {
            // Mapping coordinates for the vertices
            0.0f, 1.0f,     // top left     (V2)
            0.0f, 0.0f,     // bottom left  (V1)
            1.0f, 1.0f,     // top right    (V4)
            1.0f, 0.0f      // bottom right (V3)
    };

    private static final int COORDS_PER_VERTEX_ = 3;
    private static final int VERTEX_COUNT_ = squareCoords.length / COORDS_PER_VERTEX_;
    private static final int VERTEX_STRIDE_ = COORDS_PER_VERTEX_ * 4; // 4 bytes per vertex.
    private static final int TEX_COORDS_PER_VERTEX_ = 2;
    private static final int TEXTURE_STRIDE_ = TEX_COORDS_PER_VERTEX_ * 4;

    private FloatBuffer vertexBuffer_;
    private FloatBuffer textureBuffer_;

    private int positionHandle_;
    private int TexturePosHandle_;
    private int mvpMatrixHandle_;

    private int textureId_;

    private float[] translateMatrix_ = new float[16];
    private float[] scaleMatrix_ = new float[16];
    private float[] rotateMatrix_ = new float[16];
    private float[] modelMatrix_ = new float[16];
    private float[] matrix_ = new float[16];

    public Sprite(Context context, ShaderProgram shader,Bitmap bitmap/* int resourceId*/) {
        prepareData();

        boolean isLoaded = load(bitmap/*context, resourceId*/);
        if (!isLoaded) throw new IllegalArgumentException("Error loaded texture.");

        getHandlers(shader);
        bindData();

        Matrix.setIdentityM(translateMatrix_, 0);
        Matrix.setIdentityM(scaleMatrix_, 0);
        Matrix.setIdentityM(rotateMatrix_, 0);
        Matrix.setIdentityM(modelMatrix_, 0);
    }

    public void draw(float[] mvpMatrix) {
        Matrix.multiplyMM(matrix_, 0, mvpMatrix, 0, modelMatrix_, 0);
        // Pass the projection and view transformation to the shader.
        GLES20.glUniformMatrix4fv(mvpMatrixHandle_, 1, false, matrix_, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 1);
        // Draw the sprite.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT_);
    }

    private void buildModelMatrix() {
        Matrix.multiplyMM(modelMatrix_, 0, translateMatrix_, 0, rotateMatrix_, 0);
        Matrix.multiplyMM(modelMatrix_, 0, modelMatrix_, 0, scaleMatrix_, 0);
    }

    public void setPosition(Vector2 pos) {
        Matrix.setIdentityM(translateMatrix_, 0);
        Matrix.translateM(translateMatrix_, 0, pos.x, pos.y, 0.0f);
        buildModelMatrix();
    }

    public void setScale(float scaleVal) {
        Matrix.setIdentityM(scaleMatrix_, 0);
        Matrix.scaleM(scaleMatrix_, 0, scaleVal, scaleVal, 0.0f);
        buildModelMatrix();
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
    }

    /**
     * Загрузка текстуры.
     * @param context Контекст.
     * @param resourceId Индентификатор текстуры.
     * @return true - текстура удачно загружена.
     */
    private boolean load(Bitmap bitmap/*Context context, int resourceId*/) {
        final int[] textureIds = new int[1];
        // В массив запишет свободный номер текстуры.
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return false;
        }
/*
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);
        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureIds, 0);
            return false;
        }
*/
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Привязка текстуры.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        // Переписываем Bitmap в память видеокарты.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        setFilter();
        // Сброс привязки текстуры.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        textureId_ = textureIds[0];

        Log.i("Sprite.textureId", String.valueOf(textureId_));

        return true;
    }

    private void setFilter() {
        // На меньшем количестве пикселей экрана отображается больше кол-во пикселей текстуры.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        // На большом количестве пикселей экрана отображается меньшее кол-во пикселей текстуры.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }
    /**
     * Привязка данных (координат и т.п.) к текстуре.
     */
    private void bindData() {
        // Enable a handle to the texture vertices.
        GLES20.glEnableVertexAttribArray(positionHandle_);
        // Prepare the texture coordinate data.
        GLES20.glVertexAttribPointer(positionHandle_, COORDS_PER_VERTEX_,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE_, vertexBuffer_);

        // координаты текстур.
        GLES20.glEnableVertexAttribArray(TexturePosHandle_);
        GLES20.glVertexAttribPointer(TexturePosHandle_, TEX_COORDS_PER_VERTEX_, GLES20.GL_FLOAT,
                false, TEXTURE_STRIDE_, textureBuffer_);
    }

    /**
     * Получение Handler-ов всех атрибутов.
     */
    private void getHandlers(ShaderProgram shader) {
        positionHandle_ = shader.getAttribLocation(ShaderProgram.POSITION_ATTR);
        TexturePosHandle_ = shader.getAttribLocation(ShaderProgram.TEXCOORD_ATTR);
        mvpMatrixHandle_ = shader.getUniformLocation(ShaderProgram.MATRIX_ATTR);
    }
}