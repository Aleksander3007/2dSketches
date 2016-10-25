package com.blackteam.dsketches;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

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
        Texture texture = get(contentName);
        texture.create(context_, contentName);

        if (assets_.values().size() > 20) {
            Log.i("ContentManager", "assets_.values().size() = " + String.valueOf(assets_.values().size()));
        }
    }

    public synchronized Texture get(int contentName) {
        Texture texture = assets_.get(contentName);
        if (texture == null) {
            texture = new Texture();
            assets_.put(contentName, texture);
            Log.i("ContentManager", "texture == null");
        }

        return texture;
    }
}
