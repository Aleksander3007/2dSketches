package com.blackteam.dsketches.utils;

import android.support.v4.util.ArrayMap;

import java.util.Map;

/**
 * Вся игровая математика.
 */
public class GameMath {
    /**
     * Генерация одного из объектов из списка, содержащего вероятность выпадения каждого из этих объектов.
     * @param objectProbabilities Список объектов с вероятностью выпадения кажого из них.
     * @return Случайный объект из списка.
     */
    public static <T> T generateValue(ArrayMap<T, Float> objectProbabilities) {
        float sumProbabilities = 0;

        for (float probability : objectProbabilities.values()) {
            sumProbabilities += probability;
        }

        double randomVal = Math.random() * sumProbabilities;
        double range = 0;
        for (Map.Entry<T, Float> objectProbability : objectProbabilities.entrySet()) {
            range += objectProbability.getValue();
            if (randomVal <= range) {
                return objectProbability.getKey();
            }
        }

        return objectProbabilities.keyAt(0);
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Возращает случайное число в пределах заданных границ.
     * @param min Нижняя граница возращаемого числа.
     * @param max Верхняя граница возращаемого числа.
     * @return Случайное число.
     */
    public static double getRandom(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static Vector2 add(final Vector2 v1, final Vector2 v2) {
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vector2 sub(final Vector2 v1, final Vector2 v2) {
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }

    public static Vector2 mult(final Vector2 v1, final Vector2 v2) {
        return new Vector2(v1.x * v2.x, v1.y * v2.y);
    }

    public static Vector2 mult(final Vector2 v, final float factor) {
        return new Vector2(v.x * factor, v.y * factor);
    }

    public static Vector2 div(final Vector2 v, final float divider) {
        return new Vector2(v.x / divider, v.y / divider);
    }
}
