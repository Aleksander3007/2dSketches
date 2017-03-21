package com.blackteam.dsketches;

import com.blackteam.dsketches.models.Achievement;

import org.junit.Assert;
import org.junit.Test;

public class AchievementTest {

    @Test
    public void testEquals() throws Exception {
        Achievement achievement;

        final String SKETCH_1 = "SKETCH_1";
        final String SKETCH_2 = "SKETCH_2";
        final String SKETCH_NONE = Achievement.ANY_SKETCH;

        // Проверка sketch ачивки.
        achievement = new Achievement();
        achievement.setSketchType(SKETCH_1);
        Assert.assertTrue(achievement.equals(SKETCH_1, 0, 0));
        Assert.assertTrue(achievement.equals(SKETCH_1, 200, 0));
        Assert.assertTrue(achievement.equals(SKETCH_1, 0, 60));
        Assert.assertTrue(achievement.equals(SKETCH_1, 500, 60));
        Assert.assertFalse(achievement.equals(SKETCH_NONE, 0, 0));
        Assert.assertFalse(achievement.equals(SKETCH_2, 0, 0));

        // Проверка score ачивки.
        achievement = new Achievement();
        achievement.setScore(1000);
        Assert.assertTrue(achievement.equals(SKETCH_NONE, 1000, 0));
        Assert.assertTrue(achievement.equals(SKETCH_1, 1000, 0));
        Assert.assertTrue(achievement.equals(SKETCH_NONE, 1000, 60));
        Assert.assertTrue(achievement.equals(SKETCH_1, 1000, 60));
        Assert.assertTrue(achievement.equals(SKETCH_1, 5000, 60));
        Assert.assertFalse(achievement.equals(SKETCH_NONE, 0, 0));
        Assert.assertFalse(achievement.equals(SKETCH_NONE, 50, 0));

        // Проверка profit ачивки.
        achievement = new Achievement();
        achievement.setProfit(200);
        Assert.assertTrue(achievement.equals(SKETCH_NONE, 0, 200));
        Assert.assertTrue(achievement.equals(SKETCH_1, 0, 200));
        Assert.assertTrue(achievement.equals(SKETCH_NONE, 500, 200));
        Assert.assertTrue(achievement.equals(SKETCH_1, 500, 200));
        Assert.assertTrue(achievement.equals(SKETCH_1, 500, 900));
        Assert.assertFalse(achievement.equals(SKETCH_NONE, 0, 0));
        Assert.assertFalse(achievement.equals(SKETCH_NONE, 0, 60));

        // Проверка на тройное условие.
        achievement = new Achievement();
        achievement.setSketchType(SKETCH_1);
        achievement.setScore(1000);
        achievement.setProfit(200);
        Assert.assertTrue(achievement.equals(SKETCH_1, 1000, 200));
    }
}