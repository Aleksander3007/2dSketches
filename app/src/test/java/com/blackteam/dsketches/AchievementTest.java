package com.blackteam.dsketches;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.creation.util.SearchingClassLoader;

import static org.junit.Assert.*;

public class AchievementTest {

    @Test
    public void testEquals() throws Exception {
        Achievement achievement;

        // Проверка sketch ачивки.
        achievement = new Achievement();
        achievement.setSketchType(Sketch.Types.ROW_5);
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 0, 0));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 200, 0));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 0, 60));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 500, 60));
        Assert.assertFalse(achievement.equals(Sketch.Types.NONE, 0, 0));
        Assert.assertFalse(achievement.equals(Sketch.Types.ROW_3, 0, 0));

        // Проверка score ачивки.
        achievement = new Achievement();
        achievement.setScore(1000);
        Assert.assertTrue(achievement.equals(Sketch.Types.NONE, 1000, 0));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 1000, 0));
        Assert.assertTrue(achievement.equals(Sketch.Types.NONE, 1000, 60));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 1000, 60));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 5000, 60));
        Assert.assertFalse(achievement.equals(Sketch.Types.NONE, 0, 0));
        Assert.assertFalse(achievement.equals(Sketch.Types.NONE, 50, 0));

        // Проверка profit ачивки.
        achievement = new Achievement();
        achievement.setProfit(200);
        Assert.assertTrue(achievement.equals(Sketch.Types.NONE, 0, 200));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 0, 200));
        Assert.assertTrue(achievement.equals(Sketch.Types.NONE, 500, 200));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 500, 200));
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 500, 900));
        Assert.assertFalse(achievement.equals(Sketch.Types.NONE, 0, 0));
        Assert.assertFalse(achievement.equals(Sketch.Types.NONE, 0, 60));

        // Проверка на тройное условие.
        achievement = new Achievement();
        achievement.setSketchType(Sketch.Types.ROW_5);
        achievement.setScore(1000);
        achievement.setProfit(200);
        Assert.assertTrue(achievement.equals(Sketch.Types.ROW_5, 1000, 200));
    }
}