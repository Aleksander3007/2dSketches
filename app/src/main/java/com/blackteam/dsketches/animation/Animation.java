package com.blackteam.dsketches.animation;

import com.blackteam.dsketches.gui.TextureRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для управления анимацией спрайта, т.е. отвечает за смену текстур в спрайте с заданной частотой.
 */
public class Animation {

    /** Определяет каким образом будет проигрываться анимация. */
    public enum PlayMode {
        NORMAL,
        LOOP
    }
    private PlayMode playMode = PlayMode.NORMAL;

    private ArrayList<TextureRegion> keyFrames_;
    private float frameDuration_;
    /** Текущее время анимации. */
    public float time_;
    private int lastFrameNo_;

    /**
     * Создание анимации.
     * @param frameDuration Время длительности кадра в мс.
     * @param keyFrames Массив кадров анимации.
     * @param playMode Способ проигрования анимации.
     */
    public Animation(final float frameDuration, List<TextureRegion> keyFrames, final PlayMode playMode) {
        assert (keyFrames != null);
        this.frameDuration_ = frameDuration;
        keyFrames_ = new ArrayList<>(keyFrames);
        this.playMode = playMode;
        lastFrameNo_ = 0;
    }

    public void update(final float elapsedTime) {
        time_ += elapsedTime;
        int frameNo = (int)(time_ / frameDuration_);
        switch (playMode) {
            case NORMAL:
                frameNo = Math.min(keyFrames_.size() - 1, frameNo);
                break;
            case LOOP:
                frameNo = frameNo % keyFrames_.size();
                break;
        }

        lastFrameNo_ = frameNo;
    }

    public TextureRegion getKeyFrame() {
        return keyFrames_.get(lastFrameNo_);
    }
}
