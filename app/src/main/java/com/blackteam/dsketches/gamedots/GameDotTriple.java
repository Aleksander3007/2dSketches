package com.blackteam.dsketches.gamedots;

import com.blackteam.dsketches.ContentManager;
import com.blackteam.dsketches.utils.Vector2;

import java.util.List;

/**
 * Игровая точка с эффектом утроения profit.
 */
public class GameDotTriple extends GameDot {
    public GameDotTriple(Types dotType, SpecTypes dotSpecType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, dotSpecType, pos, rowNo, colNo, contents);
    }

    @Override
    public int affectFactor(int originalFactor) {
        return 3 * originalFactor;
    }
}
