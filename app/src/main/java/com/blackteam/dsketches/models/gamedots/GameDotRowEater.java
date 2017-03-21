package com.blackteam.dsketches.models.gamedots;

import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Vector2;

import java.util.HashSet;
import java.util.Set;

/**
 * Игровая точка с эффектом разрушения соседей по строке.
 */
public class GameDotRowEater extends GameDot {
    public GameDotRowEater(Types dotType, SpecTypes dotSpecType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, dotSpecType, pos, rowNo, colNo, contents);
    }

    public Set<GameDot> affectDots(GameDot[][] gameDots) {
        Set<GameDot> markedDots = new HashSet<>();
        for (int iCol = 0; iCol < gameDots[getRowNo()].length; iCol++) {
            if (iCol != getColNo()) {
                markedDots.add(gameDots[getRowNo()][iCol]);
            }
        }
        return markedDots;
    }
}
