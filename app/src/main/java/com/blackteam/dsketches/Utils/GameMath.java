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
        int sumProbabilities = 0;

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
}
