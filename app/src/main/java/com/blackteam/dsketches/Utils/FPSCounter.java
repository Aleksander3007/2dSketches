package com.blackteam.dsketches.Utils;

public class FPSCounter {
    private static long lastFrame = System.nanoTime();
    public static float FPS = 0;

    public static float logFrame() {
        long time = (System.nanoTime() - lastFrame);
        FPS = 1/(time/1000000000.0f);
        lastFrame = System.nanoTime();
        return FPS;
    }
}
