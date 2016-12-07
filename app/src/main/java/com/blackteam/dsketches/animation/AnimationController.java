package com.blackteam.dsketches.animation;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.ArrayList;

/**
 * Класс для управления анимацией объекта. Отвечает для изменение положения, угла поворота, изменение
 * alpha-каналов, текстур и т.п.
 */
public class AnimationController {
    private Animation animation_;
    private ArrayList<AnimationSet> animationSets_ = new ArrayList<>();

    public AnimationController(AnimationSet... animationSets) {
        this.animation_ = null;
        for (AnimationSet animationSet : animationSets) {
            this.animationSets_.add(new AnimationSet(animationSet));
        }
    }

    public AnimationController(Animation animation) {
        this.animation_ = animation;
        this.animationSets_ = null;
    }

    public void update(DisplayableObject animationObject, final float elapsedTime) {
        for (AnimationSet animationSet : animationSets_)
            updateAnimationSet(animationObject, animationSet, elapsedTime);

        if (animation_ != null) {
            animation_.update(elapsedTime);
            animationObject.setTexture(animation_.getKeyFrame());
        }
    }

    private void updateAnimationSet(DisplayableObject animationObject,
                                    AnimationSet animationSet, final float elapsedTime) {
        animationSet.update(elapsedTime);
        switch (animationSet.getValueType()) {
            case TRANSLATE:
                break;
            case ROTATE:
                break;
            case SCALE:
                Vector2 newVal = animationSet.getValues();
                Size2 newSize = new Size2(newVal.x, newVal.y);
                animationObject.setSize(newSize);
                break;
            case SCALE_CENTER:
                newVal = animationSet.getValues();
                newSize = new Size2(newVal.x, newVal.y);
                animationObject.setSizeCenter(newSize);
                break;
            case ALPHA:
                // Берем любое значение x, т.к. его и меняли.
                animationObject.setAlpha(animationSet.getValue());
                break;
        }
    }

    public boolean isFinished() {
        for (AnimationSet animationSet : animationSets_) {
            if (!animationSet.isFinished())
                return false;
        }

        return  true;
    }
}
