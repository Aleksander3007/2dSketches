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
    private PlayMode mPlayMode = PlayMode.NORMAL;

    private ArrayList<TextureRegion> mKeyFrames;
    private float mFrameDuration;
    /** Текущее время анимации. */
    private float mTime;
    private int mLastFrameNo;

    /**
     * Создание анимации.
     * @param frameDuration Время длительности кадра в мс.
     * @param keyFrames Массив кадров анимации.
     * @param playMode Способ проигрования анимации.
     */
    public Animation(final float frameDuration, List<TextureRegion> keyFrames, final PlayMode playMode) {
        assert (keyFrames != null);
        this.mFrameDuration = frameDuration;
        this.mKeyFrames = new ArrayList<>(keyFrames);
        this.mPlayMode = playMode;
        this.mLastFrameNo = 0;
    }

    public void update(final float elapsedTime) {
        mTime += elapsedTime;
        int frameNo = (int)(mTime / mFrameDuration);
        switch (mPlayMode) {
            case NORMAL:
                frameNo = Math.min(mKeyFrames.size() - 1, frameNo);
                break;
            case LOOP:
                frameNo = frameNo % mKeyFrames.size();
                break;
        }

        mLastFrameNo = frameNo;
    }

    public TextureRegion getKeyFrame() {
        return mKeyFrames.get(mLastFrameNo);
    }
}
