package com.blackteam.dsketches;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.blackteam.dsketches.gui.Texture;

/**
 * Управление загрузкой контента (например, текстурами).
 */
public class ContentManager {
    /** TODO: Тут необходимо подумать какое значение необходимо выставить (128 - чтобы точно хватило). */
    private static final int DEFAULT_SIZE = 128;
    private Context context_;
    private ArrayMap<Integer, Texture> assets_ = new ArrayMap<>(DEFAULT_SIZE);

    public ContentManager(Context context) {
        this.context_ = context;
    }

    public void load(int contentName) {
        Texture texture = get(contentName);
        texture.create(context_, contentName);
    }

    public synchronized Texture get(int contentName) {
        Texture texture = assets_.get(contentName);
        if (texture == null) {
            texture = new Texture();
            assets_.put(contentName, texture);
        }

        return texture;
    }
}
