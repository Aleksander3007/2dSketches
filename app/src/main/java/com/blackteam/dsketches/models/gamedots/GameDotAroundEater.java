package com.blackteam.dsketches.models.gamedots;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.HashSet;
import java.util.Set;

/**
 * Игровая точка с эффектом разрушения окружающих соседей.
 */
public class GameDotAroundEater extends SpecGameDot {

    public static final String NAME = "AROUND_EATER";

    public GameDotAroundEater(Types dotType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, pos, rowNo, colNo, contents);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
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

    @Override
    public DisplayableObject getDestroyAnimation(Size2 dotSize, Size2 boxSize) {
        Size2 newSize = new Size2(3 * dotSize.width, 3 * dotSize.height);
        return createScaleAnimation(dotSize, newSize);
    }
}
