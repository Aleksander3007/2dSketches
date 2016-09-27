package com.blackteam.dsketches;

/**
 * Created by СКБ4-3 on 26.09.2016.
 */
public class Vector2 {
    public float x;
    public float y;

    public Vector2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 src) {
        this.x = src.x;
        this.y = src.y;
    }
}
