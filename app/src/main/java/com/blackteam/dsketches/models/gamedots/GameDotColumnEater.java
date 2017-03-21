package com.blackteam.dsketches.models.gamedots;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.HashSet;
import java.util.Set;

/**
 * Игровая точка с эффектом разрушения соседей по столбцу.
 */
public class GameDotColumnEater extends SpecGameDot {
    public GameDotColumnEater(Types dotType, SpecTypes dotSpecType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, dotSpecType, pos, rowNo, colNo, contents);
    }

    @Override
    public Set<GameDot> affectDots(GameDot[][] gameDots) {
        Set<GameDot> markedDots = new HashSet<>();
        for (int iRow = 0; iRow < gameDots.length; iRow++) {
            if (iRow != getRowNo()) {
                markedDots.add(gameDots[iRow][getColNo()]);
            }
        }
        return markedDots;
    }

    @Override
    public DisplayableObject getDestroyAnimation(Size2 dotSize, Size2 boxSize) {
        Size2 newSize = new Size2(dotSize.width, 2 * boxSize.height);
        return createScaleAnimation(dotSize, newSize);
    }
}
