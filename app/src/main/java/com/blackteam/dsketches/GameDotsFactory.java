package com.blackteam.dsketches;

import android.support.v4.util.ArrayMap;

import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.models.gamedots.GameDot;
import com.blackteam.dsketches.models.gamedots.GameDotAroundEater;
import com.blackteam.dsketches.models.gamedots.GameDotColumnEater;
import com.blackteam.dsketches.models.gamedots.GameDotDouble;
import com.blackteam.dsketches.models.gamedots.GameDotRowEater;
import com.blackteam.dsketches.models.gamedots.GameDotTriple;
import com.blackteam.dsketches.utils.GameMath;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Фабрика по созданию игровых точек.
 * Идея - Простая фабрика.
 */
public class GameDotsFactory {

    /** Ширина текстуры. */
    public static final int TEX_WIDTH = 256;
    /** Высота текстуры. */
    public static final int TEX_HEIGHT = 256;

    /**
     * Создать игровую точку.
     * @param dotType тип.
     * @param dotSpecType специальный тип.
     * @param rowNo номер строки.
     * @param colNo номер столбца.
     * @param dotPos позиция точки.
     */
    public static GameDot createDot(GameDot.Types dotType, String dotSpecType,
                                    int rowNo, int colNo, Vector2 dotPos, ContentManager contents) {
        switch (dotSpecType) {
            case GameDotDouble.NAME:
                return new GameDotDouble(dotType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
            case GameDotTriple.NAME:
                return new GameDotTriple(dotType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
            case GameDotRowEater.NAME:
                return new GameDotRowEater(dotType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
            case GameDotColumnEater.NAME:
                return new GameDotColumnEater(dotType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
            case GameDotAroundEater.NAME:
                return new GameDotAroundEater(dotType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
            default:
                return new GameDot(dotType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
        }
    }
    /**
     * Создать игровую точку.
     * @param dotType тип.
     * @param dotSpecType специальный тип.
     * @param rowNo номер строки.
     * @param colNo номер столбца.
     * @param dotPos позиция точки.
     * @param dotSize размер точки.
     */
    public static GameDot createDot(GameDot.Types dotType, String dotSpecType,
                          int rowNo, int colNo, Vector2 dotPos, float dotSize, ContentManager contents) {

        GameDot gameDot = createDot(dotType, dotSpecType, rowNo, colNo, dotPos, contents);
        gameDot.setSize(dotSize);
        return gameDot;
    }

    public static TextureRegion getTextureRegion(GameDot.Types dotType, ContentManager contents) {
        return new TextureRegion(
                contents.get(R.drawable.dots_theme1),
                getTexturePosition(dotType),
                new Size2(TEX_WIDTH, TEX_HEIGHT)
        );
    }

    public static TextureRegion getSpecTextureRegion(String dotSpecType, ContentManager contents) {
        return new TextureRegion(
                contents.get(R.drawable.dots_theme1),
                getSpecTexturePosition(dotSpecType),
                new Size2(TEX_WIDTH, TEX_WIDTH)
        );
    }

    public static GameDot.Types generateDotType() {
        ArrayMap<GameDot.Types, Float> dotTypeProbabilities = new ArrayMap<>();
        dotTypeProbabilities.put(GameDot.Types.TYPE1, 24f);
        dotTypeProbabilities.put(GameDot.Types.TYPE2, 24f);
        dotTypeProbabilities.put(GameDot.Types.TYPE3, 24f);
        dotTypeProbabilities.put(GameDot.Types.TYPE4, 24f);
        dotTypeProbabilities.put(GameDot.Types.UNIVERSAL, 74f); // original: 4f

        return GameMath.generateValue(dotTypeProbabilities);
    }

    public static String generateDotSpecType() {
        ArrayMap<String, Float> dotTypeProbabilities = new ArrayMap<>();
        dotTypeProbabilities.put(GameDot.NAME, 93f);
        dotTypeProbabilities.put(GameDotDouble.NAME, 2f);
        dotTypeProbabilities.put(GameDotTriple.NAME, 0.5f);
        dotTypeProbabilities.put(GameDotRowEater.NAME, 1.5f);
        dotTypeProbabilities.put(GameDotColumnEater.NAME, 1.5f);
        dotTypeProbabilities.put(GameDotAroundEater.NAME, 1.5f);

        return GameMath.generateValue(dotTypeProbabilities);
    }

    private static Vector2 getTexturePosition(GameDot.Types type) {
        int x = 0;

        switch (type) {
            case TYPE1:
                x = 0;
                break;
            case TYPE2:
                x = TEX_HEIGHT;
                break;
            case TYPE3:
                x = 2 * TEX_HEIGHT;
                break;
            case TYPE4:
                x = 3 * TEX_HEIGHT;
                break;
            case UNIVERSAL:
                x = 4 * TEX_HEIGHT;
                break;
        }

        return new Vector2(x, 0);
    }

    public static Vector2 getSpecTexturePosition(String specType) {
        int x = 0;

        switch (specType) {
            case GameDot.NAME:
                x = 0; // Берём этот, но на деле мы ничего не отрисовываем.
                break;
            case GameDotDouble.NAME:
                x = 0;
                break;
            case GameDotTriple.NAME:
                x = TEX_WIDTH;
                break;
            case GameDotRowEater.NAME:
                x = 2 * TEX_WIDTH;
                break;
            case GameDotColumnEater.NAME:
                x = 3 * TEX_WIDTH;
                break;
            case GameDotAroundEater.NAME:
                x = 4 * TEX_WIDTH;
                break;
        }

        return new Vector2(x, TEX_HEIGHT);
    }
}
