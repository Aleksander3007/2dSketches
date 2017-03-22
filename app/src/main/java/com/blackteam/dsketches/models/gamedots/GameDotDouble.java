package com.blackteam.dsketches.models.gamedots;

import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Игровая точка с эффектом удвоения profit.
 */
public class GameDotDouble extends SpecGameDot {

    public static final String NAME = "DOUBLE";

    public GameDotDouble(Types dotType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, pos, rowNo, colNo, contents);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int affectFactor(int originalFactor) {
        return 2 * originalFactor;
    }
}
