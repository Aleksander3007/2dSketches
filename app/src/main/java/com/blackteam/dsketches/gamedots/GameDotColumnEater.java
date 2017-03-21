package com.blackteam.dsketches.gamedots;

import com.blackteam.dsketches.ContentManager;
import com.blackteam.dsketches.utils.Vector2;

import java.util.HashSet;
import java.util.Set;

/**
 * Игровая точка с эффектом разрушения соседей по столбцу.
 */
public class GameDotColumnEater extends GameDot {
    public GameDotColumnEater(Types dotType, SpecTypes dotSpecType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, dotSpecType, pos, rowNo, colNo, contents);
    }

    public Set<GameDot> affectDots(GameDot[][] gameDots) {
        Set<GameDot> markedDots = new HashSet<>();
        for (int iRow = 0; iRow < gameDots.length; iRow++) {
            if (iRow != getRowNo()) {
                markedDots.add(gameDots[iRow][getColNo()]);
            }
        }
        return markedDots;
    }
}
