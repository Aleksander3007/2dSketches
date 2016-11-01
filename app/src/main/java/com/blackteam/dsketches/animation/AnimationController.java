package com.blackteam.dsketches.animation;

import com.blackteam.dsketches.gui.DisplayableObject;

/**
 * Класс для управления анимацией объекта. Отвечает для изменение положения, угла поворота, изменение
 * alpha-каналов, текстур и т.п.
 */
public class AnimationController {
    private Animation animation_;
    private AnimationSet animationSet_;

    private DisplayableObject animationObject_;

    public AnimationController(DisplayableObject animationObject, AnimationSet animationSet) {
        this.animationObject_ = animationObject;
        this.animation_ = null;
        this.animationSet_ = new AnimationSet(animationSet);
    }

    public AnimationController(DisplayableObject animationObject, Animation animation) {
        this.animationObject_ = animationObject;
        this.animation_ = animation;
        this.animationSet_ = null;
    }

    public void update(final float elapsedTime) {
        if (animationSet_ != null) {
            animationSet_.update(elapsedTime);
            switch (animationSet_.getValueType()) {
                case TRANSLATE:
                    break;
                case ROTATE:
                    break;
                case SCALE:
                    break;
                case ALPHA:
                    animationObject_.setAlpha(animationSet_.getValue());
                    break;
            }
        }

        if (animation_ != null) {
            animation_.update(elapsedTime);
            animationObject_.setTexture(animation_.getKeyFrame());
        }
    }
}
