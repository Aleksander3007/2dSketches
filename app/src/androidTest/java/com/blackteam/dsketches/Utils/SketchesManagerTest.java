package com.blackteam.dsketches.utils;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.blackteam.dsketches.GameDot;
import com.blackteam.dsketches.Sketch;
import com.blackteam.dsketches.SketchesManager;
import com.blackteam.dsketches.gui.Texture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


@RunWith(AndroidJUnit4.class)
public class SketchesManagerTest {

    private SketchesManager sketchesManager_;

    @Before
    public void setUp() throws Exception {
        sketchesManager_ = new SketchesManager();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindSketch() throws Exception {
        ArrayList<GameDot> gameDots = new ArrayList<>();
        Vector2 orbPos = new Vector2(0f, 0f);
        GameDot.Types orbType = GameDot.Types.TYPE1;
        GameDot.SpecTypes orbSpecType = GameDot.SpecTypes.NONE;

        Bitmap fakeBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_4444);
        Texture fakeTexture = new Texture(fakeBitmap);

        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 0, fakeTexture));
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 1, fakeTexture));
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 2, fakeTexture));

        // Проверка, что sketch найден.
        Sketch sketch = sketchesManager_.findSketch(gameDots);
        org.junit.Assert.assertEquals(Sketch.Types.ROW_3, sketch.getType());

        // Проверка, что sketch НЕ найден.
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 3, 0, fakeTexture));
        sketch = sketchesManager_.findSketch(gameDots);
        org.junit.Assert.assertEquals(Sketch.Types.NONE, sketch.getType());

        gameDots.clear();
    }
}