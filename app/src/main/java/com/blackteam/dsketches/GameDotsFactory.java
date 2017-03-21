package com.blackteam.dsketches;

import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.models.gamedots.GameDot;
import com.blackteam.dsketches.models.gamedots.GameDotAroundEater;
import com.blackteam.dsketches.models.gamedots.GameDotColumnEater;
import com.blackteam.dsketches.models.gamedots.GameDotDouble;
import com.blackteam.dsketches.models.gamedots.GameDotRowEater;
import com.blackteam.dsketches.models.gamedots.GameDotTriple;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Фабрика по созданию игровых точек.
 * Идея - Простая фабрика.
 */
public class GameDotsFactory {
    /**
     * Создать GameDot.
     * @param dotType Тип.
     * @param dotSpecType Специальный тип.
     * @param rowNo Номер строки.
     * @param colNo Номер столбца.
     */
    public static GameDot createDot(GameDot.Types dotType, GameDot.SpecTypes dotSpecType,
                          int rowNo, int colNo, Vector2 dotPos, float dotSize, ContentManager contents) {
        GameDot gameDot;
        switch (dotSpecType) {
            case DOUBLE:
                gameDot = new GameDotDouble(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                    contents
                );
                break;
            case TRIPLE:
                gameDot = new GameDotTriple(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
                break;
            case ROW_EATER:
                gameDot = new GameDotRowEater(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
                break;
            case COLUMN_EATER:
                gameDot = new GameDotColumnEater(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
                break;
            case AROUND_EATER:
                gameDot = new GameDotAroundEater(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
                break;
            default:
                gameDot = new GameDot(dotType, dotSpecType,
                        dotPos,
                        rowNo, colNo,
                        contents
                );
        }

        gameDot.setSize(dotSize);
        return gameDot;
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
            case UNIVERSAL:
                x = 4 * GameDot.TEX_HEIGHT;
                break;
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
                x = 0;
                break;
            case TRIPLE:
                x = GameDot.TEX_WIDTH;
                break;
            case ROW_EATER:
                x = 2 * GameDot.TEX_WIDTH;
                break;
            case COLUMN_EATER:
                x = 3 * GameDot.TEX_WIDTH;
                break;
            case AROUND_EATER:
                x = 4 * GameDot.TEX_WIDTH;
                break;
        }

        return new Vector2(x, GameDot.TEX_HEIGHT);
    }
}
