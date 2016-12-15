package com.blackteam.dsketches;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.blackteam.dsketches.gui.Texture;

/**
 * Управление загрузкой контента (например, текстурами).
 */
public class ContentManager {
    private Context mContext;
    private ArrayMap<Integer, Texture> mAssets = new ArrayMap<>();

    public ContentManager(Context context) {
        this.mContext = context;
    }

    public void load(int contentName) {
        Texture texture = get(contentName);
        texture.create(mContext, contentName);

        if (mAssets.values().size() > 20) {
            Log.i("ContentManager", "mAssets.values().size() = " + String.valueOf(mAssets.values().size()));
        }
    }

    public synchronized Texture get(int contentName) {
        Texture texture = mAssets.get(contentName);
        if (texture == null) {
            texture = new Texture();
            mAssets.put(contentName, texture);
            Log.i("ContentManager", "texture == null");
        }

        return texture;
    }
}
