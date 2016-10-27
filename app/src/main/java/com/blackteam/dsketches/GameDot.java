package com.blackteam.dsketches;

import android.widget.GridLayout;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

public class GameDot {
    public enum Types {
        TYPE1,
        TYPE2,
        TYPE3,
        TYPE4,
        UNIVERSAL
    }

    public enum SpecTypes {
        NONE, // Без эффекта.
        DOUBLE, // Удвоение profit.
        TRIPLE, // Утроение profit.
        AROUND_EATER, // Разрушаются вокруг все соседи.
        ROWS_EATER, // Разрушаются соседи по строке.
        COLUMNS_EATER // Разрушаются соседи по столбцу.
    }

    /** Ширина текстуры. */
    public static final int TEX_WIDTH = 256;
    /** Высота текстуры. */
    public static final int TEX_HEIGHT = 256;

    private GameDot.Types type_;
    private GameDot.SpecTypes specType_;
    private int rowNo_;
    private int colNo_;

    /** Главный объект, отображающий игровую точку. */
    private DisplayableObject mainObject_;
    /** Объект, отображающий специальность игровой точки. */
    private DisplayableObject specObject_;

    public GameDot(GameDot.Types dotType, GameDot.SpecTypes dotSpecType, Vector2 pos,
                   int rowNo, int colNo, ContentManager contents) {

        this.type_ = dotType;
        this.specType_ = dotSpecType;
        this.rowNo_ = rowNo;
        this.colNo_ = colNo;

        // TODO: NOW: Пробуем компилить, потом дальше делаем!

        TextureRegion textureRegion = new TextureRegion(
                contents.get(R.drawable.dots_theme1),
                getTexturePosition(dotType),
                new Size2(TEX_WIDTH, TEX_HEIGHT)
        );

        // TODO: Сделать конструктор DisplayableObject в которой подается сразу TextureRegion.
        mainObject_ = new DisplayableObject(pos, textureRegion.getTexture(),
                textureRegion.getPos().x, textureRegion.getPos().y,
                textureRegion.getSize().width, textureRegion.getSize().height
        );

        if (specType_ != SpecTypes.NONE) {
            TextureRegion specTextureRegion = new TextureRegion(
                    contents.get(R.drawable.dots_theme1),
                    getSpecTexturePosition(dotSpecType),
                    new Size2(TEX_WIDTH, TEX_HEIGHT)
            );

            specObject_ = new DisplayableObject(pos, specTextureRegion.getTexture(),
                    specTextureRegion.getPos().x, specTextureRegion.getPos().y,
                    specTextureRegion.getSize().width, specTextureRegion.getSize().height
            );
        }
    }

    public int getColNo() {
        return colNo_;
    }

    public int getRowNo() {
        return rowNo_;
    }

    public GameDot.Types getType() {
        return this.type_;
    }

    public void setType(GameDot.Types dotType) {
        this.type_ = dotType;
    }

    public GameDot.SpecTypes getSpecType() {
        return this.specType_;
    }

    public static Vector2 getTexturePosition(GameDot.Types type) {
        int x = 0;

        switch (type) {
            case TYPE1:
                x = 0;
                break;
            case TYPE2:
                x = GameDot.TEX_HEIGHT;
                break;
            case TYPE3:
                x = 2 * GameDot.TEX_HEIGHT;
                break;
            case TYPE4:
                x = 3 * GameDot.TEX_HEIGHT;
                break;
            default:
                x = 0;
        }

        return new Vector2(x, 0);
    }

    public static Vector2 getSpecTexturePosition(GameDot.SpecTypes specType) {
        int x = 0;

        switch (specType) {
            case NONE:
                x = 0; // Берём этот, но на деле мы ничего не отрисовываем.
                break;
            case DOUBLE:
                break;
            case TRIPLE:
                break;
            case AROUND_EATER:
                break;
            case ROWS_EATER:
                break;
            case COLUMNS_EATER:
                break;
        }

        return new Vector2(x, TEX_HEIGHT);
    }

    public void setSize(float size) {
        mainObject_.setSize(size, size);
        if (specType_ != SpecTypes.NONE)
            specObject_.setSize(size, size);
    }

    public static Types convertToType(String gameDotTypeStr) {
        return Enum.valueOf(GameDot.Types.class, gameDotTypeStr);
    }

    public static SpecTypes convertToSpecType(String gameDotSpecTypeStr) {
        return Enum.valueOf(GameDot.SpecTypes.class, gameDotSpecTypeStr);
    }

    public void setPosition(Vector2 dotPos) {
        mainObject_.setPosition(dotPos);
        if (specType_ != SpecTypes.NONE)
            specObject_.setPosition(dotPos);
    }

    public void draw(float[] mvpMatrix, ShaderProgram shader) {
        mainObject_.draw(mvpMatrix, shader);
        if (specType_ != SpecTypes.NONE)
            specObject_.draw(mvpMatrix, shader);
    }

    public boolean hit(Vector2 coords) {
        return mainObject_.hit(coords);
    }

    public float getX() {
        return mainObject_.getX();
    }

    public float getY() {
        return mainObject_.getY();
    }

    public float getWidth() {
        return mainObject_.getWidth();
    }

    public float getHeight() {
        return mainObject_.getHeight();
    }
}
