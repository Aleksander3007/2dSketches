package com.blackteam.dsketches.utils;

/**
 * 2D вектор.
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

    public Vector2 add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public Vector2 mul(Vector2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }
}
