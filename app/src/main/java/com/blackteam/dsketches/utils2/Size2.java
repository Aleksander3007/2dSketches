package com.blackteam.dsketches.utils;

/**
 * 2-х мерный размер.
 */
public class Size2 {
    public float width;
    public float height;

    public Size2(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Size2(Size2 src) {
        this.width = src.width;
        this.height = src.height;
    }
}
