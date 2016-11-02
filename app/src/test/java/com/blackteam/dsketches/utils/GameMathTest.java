package com.blackteam.dsketches.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Тестирование GameMath.
 */
public class GameMathTest {

    @Test
    public void testAdd() throws Exception {
        Vector2 v1 = new Vector2(-0.1f, 0.2f);
        Vector2 v2 = new Vector2(3f, 40f);

        Vector2 result = GameMath.add(v1, v2);

        Assert.assertEquals(v1.x + v2.x, result.x, 0.001f);
        Assert.assertEquals(v1.y + v2.y, result.y, 0.001f);
    }

    @Test
    public void testSub() throws Exception {
        Vector2 v1 = new Vector2(-0.1f, 0.2f);
        Vector2 v2 = new Vector2(3f, 40f);

        Vector2 result = GameMath.sub(v1, v2);

        Assert.assertEquals(v1.x - v2.x, result.x, 0.001f);
        Assert.assertEquals(v1.y - v2.y, result.y, 0.001f);
    }

    @Test
    public void testMult() throws Exception {
        Vector2 v1 = new Vector2(-0.1f, 0.2f);
        Vector2 v2 = new Vector2(3f, 40f);

        Vector2 result = GameMath.mult(v1, v2);

        Assert.assertEquals(v1.x * v2.x, result.x, 0.001f);
        Assert.assertEquals(v1.y * v2.y, result.y, 0.001f);
    }
}