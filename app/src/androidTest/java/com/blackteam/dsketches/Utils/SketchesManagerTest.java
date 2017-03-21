package com.blackteam.dsketches.Utils;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.models.gamedots.GameDot;
import com.blackteam.dsketches.models.Sketch;
import com.blackteam.dsketches.managers.SketchesManager;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.utils.Vector2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


@RunWith(AndroidJUnit4.class)
public class SketchesManagerTest {

    private SketchesManager sketchesManager_;
    private ContentManager fakeContents_ = new ContentManager(null);

    @Before
    public void setUp() throws Exception {
        sketchesManager_ = new SketchesManager(InstrumentationRegistry.getContext());
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

        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 0, fakeContents_));
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 1, fakeContents_));
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 2, fakeContents_));
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 3, fakeContents_));
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 0, 4, fakeContents_));

        // Проверка, что sketch найден.
        Sketch sketch = sketchesManager_.findSketch(gameDots);
        org.junit.Assert.assertEquals("row_5", sketch.getName());

        // Проверка, что sketch НЕ найден.
        gameDots.add(new GameDot(orbType, orbSpecType, orbPos, 3, 0, fakeContents_));
        sketch = sketchesManager_.findSketch(gameDots);
        org.junit.Assert.assertEquals(SketchesManager.SKETCH_NULL.getName(), sketch.getName());

        gameDots.clear();
    }
}