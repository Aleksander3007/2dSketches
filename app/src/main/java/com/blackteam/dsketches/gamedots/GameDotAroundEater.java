package com.blackteam.dsketches.gamedots;

import com.blackteam.dsketches.ContentManager;
import com.blackteam.dsketches.utils.Vector2;

import java.util.HashSet;
import java.util.Set;

/**
 * Игровая точка с эффектом разрушения окружающих соседей.
 */
public class GameDotAroundEater extends GameDot {
    public GameDotAroundEater(Types dotType, SpecTypes dotSpecType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, dotSpecType, pos, rowNo, colNo, contents);
    }

    public Set<GameDot> affectDots(GameDot[][] gameDots) {
        Set<GameDot> markedDots = new HashSet<>();

        for (int iRowShift = -1; iRowShift <= 1; iRowShift++) {
            for (int iColShift = -1; iColShift <= 1; iColShift++) {

                int rowNo = getRowNo() + iRowShift;
                int colNo = getColNo() + iColShift;

                // Проверяем, что полученный индекс является допустимым.
                if (rowNo >= 0 && rowNo < gameDots.length) {
                    if (colNo >= 0 && colNo < gameDots[rowNo].length) {
                        markedDots.add(gameDots[rowNo][colNo]);
                    }
                }

            }
        }

        return markedDots;
    }
}
