package com.blackteam.dsketches.gui;

import android.opengl.GLES20;

/**
 * Для работы с vertex и fragment шейдерами.
 * Создание программы шейдера очень дорогостоящая операция!
 */
public class ShaderProgram {
    public static final String POSITION_ATTR = "v_Position";
    public static final String TEXCOORD_ATTR = "a_Texture";
    public static final String MATRIX_ATTR = "u_Matrix";
    public static final String ALPHA_FACTOR_ATTR = "v_alphaFactor";

    private static final String VERTEX_SHADER_CODE_ =
            "uniform mat4 " + MATRIX_ATTR + ";" +
                    "attribute vec4 " + POSITION_ATTR + ";" +
                    "varying vec2 v_Texture;" +
                    "attribute vec2 " + TEXCOORD_ATTR + ";" +
                    "void main() {" +
                    "  gl_Position = " + MATRIX_ATTR + " * " + POSITION_ATTR + ";" +
                    "  v_Texture = " + TEXCOORD_ATTR + ";" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE_ =
            "precision mediump float;" +
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_Texture;" +
                    "uniform float v_alphaFactor;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(u_TextureUnit, v_Texture);" +
                    " gl_FragColor.a *= v_alphaFactor;" +
                    "}";

    private int program_;

    /**
     * Загрузка и сборка шейдеров, создание новой программы и привязка к шейдерам.
     * @return true - успешно.
     */
    public boolean compile() {
        int vertexShader = load(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE_);
        int fragmentShader = load(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE_);

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

    public void begin() {
        // Add program to OpenGL ES environment.
        GLES20.glUseProgram(program_);

        // Enable a handle to the texture vertices.
        GLES20.glEnableVertexAttribArray(getAttribLocation(POSITION_ATTR));
        // координаты текстур.
        GLES20.glEnableVertexAttribArray(getAttribLocation(TEXCOORD_ATTR));
    }

    public int getAttribLocation(String name) {
        return GLES20.glGetAttribLocation(program_, name);
    }

    public int getUniformLocation(String name) {
        return GLES20.glGetUniformLocation(program_, name);
    }

    /**
     * Загрузка шейдера.
     * @param type Тип шейдера.
     * @param shaderCode Код шейдера.
     * @return Шейдер.
     */
    private int load(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
