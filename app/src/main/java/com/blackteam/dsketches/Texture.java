package com.blackteam.dsketches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Текстура.
 */
public class Texture {
    private static final String VERTEX_SHADER_CODE_ =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "varying vec2 v_Texture;" +
            "attribute vec2 a_Texture;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_Texture = a_Texture;" +
            "}";

    private static final String FRAGMENT_SHADER_CODE_ =
            "precision mediump float;" +
            "uniform sampler2D u_TextureUnit;" +
            "varying vec2 v_Texture;" +
            "void main() {" +
            "  gl_FragColor = texture2D(u_TextureUnit, v_Texture);" +
            "}";

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

    private int program_;

    private int mMVPMatrixHandle_;
    private int mPositionHandle_;
    private int mTexturePosHandle_;

    private int textureId_;

    public Texture(Context context, int resourceId) {
        prepareData();

        boolean isLoaded = load(context, resourceId);
        if (!isLoaded) throw new IllegalArgumentException("Error loaded texture.");

        boolean isShaderCompiled = createShaderProgramm();
        if (!isShaderCompiled) throw new IllegalArgumentException("Error compiling shader.");

        // Add program to OpenGL ES environment.
        GLES20.glUseProgram(program_);

        getAttribLocation();

        bindData();
    }

    /**
     * Загрузка текстуры.
     * @param context Контекст.
     * @param resourceId Индентификатор текстуры.
     * @return true - текстура удачно загружена.
     */
    public boolean load(Context context, int resourceId) {
        final int[] textureIds = new int[1];
        //в массив OpenGL ES запишет свободный номер текстуры,
        // получаем свободное имя текстуры, которое будет записано в names[0]
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return false;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);
        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureIds, 0);
            return false;
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        // На меньшем количестве пикселей экрана отображается больше кол-во пикселей текстуры.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        // На большом количестве пикселей экрана отображается меньшее кол-во пикселей текстуры.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // Переписываем Bitmap в память видеокарты.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        textureId_ = textureIds[0];

        return true;
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT_);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle_, 1, false, mvpMatrix, 0);
        // Disable vertex array
        //GLES20.glDisableVertexAttribArray(mPositionHandle_);
    }

    /**
     * Создание программы шейдеров.
     * @return true - успешно.
     */
    private boolean createShaderProgramm() {
        int vertexShader = GameRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                VERTEX_SHADER_CODE_);
        int fragmentShader = GameRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                FRAGMENT_SHADER_CODE_);

        if (vertexShader == -1 || fragmentShader == -1) {
            return false;
        }

        program_ = GLES20.glCreateProgram();
        if (program_ == -1) {
            return false;
        }

        GLES20.glAttachShader(program_, vertexShader);
        GLES20.glAttachShader(program_, fragmentShader);
        GLES20.glLinkProgram(program_);

        return true;
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
     * Получение Handler-ов всех атрибутов.
     */
    private void getAttribLocation() {
        // get handle to vertex shader's vPosition member.
        mPositionHandle_ = GLES20.glGetAttribLocation(program_, "vPosition");
        mTexturePosHandle_ = GLES20.glGetAttribLocation(program_, "a_Texture");
        // get handle to shape's transformation matrix
        mMVPMatrixHandle_ = GLES20.glGetUniformLocation(program_, "uMVPMatrix");
    }

    /**
     * Привязка данных (координат и т.п.) к текстуре.
     */
    private void bindData() {
        // Enable a handle to the texture vertices.
        GLES20.glEnableVertexAttribArray(mPositionHandle_);
        // Prepare the texture coordinate data.
        GLES20.glVertexAttribPointer(mPositionHandle_, COORDS_PER_VERTEX_,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE_, vertexBuffer_);

        // координаты текстур.
        GLES20.glEnableVertexAttribArray(mTexturePosHandle_);
        GLES20.glVertexAttribPointer(mTexturePosHandle_, TEX_COORDS_PER_VERTEX_, GLES20.GL_FLOAT,
                false, TEXTURE_STRIDE_, textureBuffer_);
    }
}