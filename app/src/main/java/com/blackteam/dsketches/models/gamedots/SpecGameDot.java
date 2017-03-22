package com.blackteam.dsketches.models.gamedots;

import com.blackteam.dsketches.GameDotsFactory;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.animation.AnimationController;
import com.blackteam.dsketches.animation.AnimationSet;
import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Специальная игровая точка, которая имеет какие-либо эффекты на игровые очки, другие точки и т.д.
 */
public abstract class SpecGameDot extends GameDot {

    /** Объект, отображающий специальность игровой точки. */
    private DisplayableObject specObject_;

    /** Параметры для эффекта живой спец. точки. (мигание specObject_) */
    private static final float SPEC_MIN_ALPHA_ = 0.5f;
    private static final float SPEC_MAX_ALPHA_ = 1.0f;
    private static final float SPEC_ALPHA_TIME_ = 2000.0f; // Время на изменения alpha-канала, ms.
    private static final AnimationSet FLASHING_ANIM_SET_ = new AnimationSet(AnimationSet.ValueType.ALPHA,
            AnimationSet.PlayMode.LOOP_PINGPONG,
            SPEC_MIN_ALPHA_, SPEC_MAX_ALPHA_, SPEC_ALPHA_TIME_);

    public SpecGameDot(Types dotType, SpecTypes dotSpecType, Vector2 pos, int rowNo, int colNo, ContentManager contents) {
        super(dotType, dotSpecType, pos, rowNo, colNo, contents);

        specObject_ = new DisplayableObject(pos,
                GameDotsFactory.getSpecTextureRegion(dotSpecType, mContents));
        specObject_.setAnimation(new AnimationController(
                FILM_DEVELOPMENT_ANIM_SET_, FLASHING_ANIM_SET_
        ));

    }

    @Override
    public void setSize(float size) {
        super.setSize(size);
        specObject_.setSize(size, size);
    }

    @Override
    public void setSizeCenter(float size) {
        super.setSizeCenter(size);
        specObject_.setSizeCenter(size, size);
    }

    @Override
    public void setPosition(Vector2 dotPos) {
        super.setPosition(dotPos);
        specObject_.setPosition(dotPos);
    }

    @Override
    public void setAlpha(float alphaFactor) {
        super.setAlpha(alphaFactor);
        specObject_.setAlpha(alphaFactor);
    }

    @Override
    public void render(Graphics graphics) {
        super.render(graphics);
        specObject_.render(graphics);
    }

    @Override
    protected void moving(float elapsedTime) {
        super.moving(elapsedTime);
        specObject_.setPosition(mainObject_.getPosition());
    }
}
