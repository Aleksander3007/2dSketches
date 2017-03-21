package com.blackteam.dsketches.models.gamedots;

import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Игровая точка с эффектом утроения profit.
 */
public class GameDotTriple extends SpecGameDot {
    public GameDotTriple(Types dotType, SpecTypes dotSpecType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, dotSpecType, pos, rowNo, colNo, contents);
    }

    @Override
    public int affectFactor(int originalFactor) {
        return 3 * originalFactor;
    }
}
