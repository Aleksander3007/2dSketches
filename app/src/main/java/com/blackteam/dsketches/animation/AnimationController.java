package com.blackteam.dsketches.animation;

import com.blackteam.dsketches.gui.DisplayableObject;

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
        for (AnimationSet animationSet : animationSets)
            this.animationSets_.add(new AnimationSet(animationSet));
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
            case SCALE_X:
                animationObject.setSize(animationSet.getValue(), animationObject.getHeight());
                break;
            case SCALE:
                break;
            case ALPHA:
                animationObject.setAlpha(animationSet.getValue());
                break;
        }
    }
}
