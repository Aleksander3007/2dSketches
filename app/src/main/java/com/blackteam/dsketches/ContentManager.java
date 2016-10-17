package com.blackteam.dsketches;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.blackteam.dsketches.gui.Texture;

/**
 * Управление загрузкой контента (например, текстурами).
 */
public class ContentManager {
    private Context context_;
    private ArrayMap<Integer, Texture> assets_ = new ArrayMap<>();

    public ContentManager(Context context) {
        this.context_ = context;
    }

    public void load(int contentName) {
        Texture texture = new Texture(context_, contentName);
        assets_.put(contentName, texture);
    }

    public Texture get(int contentName) {
        return assets_.get(contentName);
    }
}
